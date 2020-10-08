package com.badlogic.gdx.setup.tables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.setup.backend.BackendClient;
import com.badlogic.gdx.setup.backend.VersionResponse;
import com.badlogic.gdx.utils.Align;
import com.ray3k.tenpatch.TenPatchDrawable;

import static com.badlogic.gdx.setup.SetupUi.*;

public class RetrieveDataLoadingTable extends Table  {
    WaitForResponseListener<VersionResponse> versionResponse;
    TenPatchDrawable tenPatchDrawable;
    Label label;
    Mode mode;
    public enum Mode {
        REQUESTING, SUCCESS, FAIL, HIDING, DONE
    }

    public RetrieveDataLoadingTable() {
        versionResponse = new WaitForResponseListener<>();
        backendClient.getVersions(versionResponse);
        
        tenPatchDrawable = new TenPatchDrawable(skin.get("loading-animation", TenPatchDrawable.class));
        Image image = new Image(tenPatchDrawable);
        add(image);
    
        row();
        label = new Label("", skin, "loading");
        label.setWrap(true);
        label.setAlignment(Align.center);
        add(label).growX();
        
        mode = Mode.REQUESTING;
        label.setText("REQUESTING VERSION");
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (tenPatchDrawable.getRegions().peek().equals(tenPatchDrawable.getKeyFrame())) {
            switch (mode) {
                case SUCCESS:
                    mode = Mode.HIDING;
                    label.setText("LOADING UI");
                    
                    clearChildren();
                    tenPatchDrawable = new TenPatchDrawable(skin.get("loading-hide", TenPatchDrawable.class));
                    Image image = new Image(tenPatchDrawable);
                    add(image);
                    row();
                    add(label);
                    
                    supportedGDXVersions = versionResponse.retrievedData.supportedGdxVersions;
                    buildVersion = versionResponse.retrievedData.backendVersion;
                    break;
                case HIDING:
                    mode = Mode.DONE;
                    
                    landingTable.populate();
                    classicProjectTable.populate();
                    slideDownTable(landingTable);
                    break;
            }
        }
    }
    
    private class WaitForResponseListener<T> implements BackendClient.IBackendResponse<T> {
        T retrievedData;
        
        WaitForResponseListener() {
            mode = Mode.REQUESTING;
        }
        
        @Override
        public void onFail(int statusCode, String errorMsg) {
            Gdx.app.postRunnable(() -> {
                mode = Mode.FAIL;
                label.setText("CANNOT CONNECT TO SERVER\nERROR CODE " + statusCode);
            });
        }
        
        @Override
        public void onSuccess(T retrievedData) {
            this.retrievedData = retrievedData;
            Gdx.app.postRunnable(() -> {
                mode = Mode.SUCCESS;
            });
        }
    }
}

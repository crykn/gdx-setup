package com.badlogic.gdx.setup.tables;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.setup.backend.BackendClient;
import com.badlogic.gdx.setup.backend.VersionResponse;
import com.ray3k.tenpatch.TenPatchDrawable;

import static com.badlogic.gdx.setup.SetupUi.*;

public class RetrieveDataLoadingTable extends Table  {
    WaitForResponseListener<VersionResponse> versionResponse;
    TenPatchDrawable tenPatchDrawable;
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
        mode = Mode.REQUESTING;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (tenPatchDrawable.getRegions().peek().equals(tenPatchDrawable.getKeyFrame())) {
            switch (mode) {
                case SUCCESS:
                    mode = Mode.HIDING;
                    clearChildren();
                    tenPatchDrawable = new TenPatchDrawable(skin.get("loading-hide", TenPatchDrawable.class));
                    Image image = new Image(tenPatchDrawable);
                    add(image);
                    
                    supportedGDXVersions = versionResponse.retrievedData.supportedGdxVersions;
                    buildVersion = versionResponse.retrievedData.backendVersion;
                    break;
                case HIDING:
                    clearChildren();
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
        int lastCode;
        
        WaitForResponseListener() {
            mode = Mode.REQUESTING;
        }
        
        @Override
        public void onFail(int statusCode, String errorMsg) {
            mode = Mode.FAIL;
            lastCode = statusCode;
        }
        
        @Override
        public void onSuccess(T retrievedData) {
            this.retrievedData = retrievedData;
            lastCode = 0;
            mode = Mode.SUCCESS;
        }
    }
}

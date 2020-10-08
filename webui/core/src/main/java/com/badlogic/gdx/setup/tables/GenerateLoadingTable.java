package com.badlogic.gdx.setup.tables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.setup.backend.BackendClient;
import com.badlogic.gdx.setup.backend.GenerateProjectParams;
import com.badlogic.gdx.setup.backend.GeneratorResponse;
import com.badlogic.gdx.setup.tables.RetrieveDataLoadingTable.Mode;
import com.badlogic.gdx.utils.Align;
import com.ray3k.tenpatch.TenPatchDrawable;

import static com.badlogic.gdx.setup.SetupUi.backendClient;
import static com.badlogic.gdx.setup.SetupUi.skin;

public class GenerateLoadingTable extends Table  {
    WaitForResponseListener<GeneratorResponse> generatorResponse;
    TenPatchDrawable tenPatchDrawable;
    Label label;
    Mode mode;
    public enum Mode {
        GENERATING, SUCCESS, FAIL, HIDING, DONE
    }

    public GenerateLoadingTable(GenerateProjectParams params) {
        generatorResponse = new WaitForResponseListener<>();
        backendClient.generateProject(params, generatorResponse);
        
        tenPatchDrawable = new TenPatchDrawable(skin.get("loading-animation", TenPatchDrawable.class));
        Image image = new Image(tenPatchDrawable);
        add(image);
    
        row();
        label = new Label("", skin, "loading");
        label.setWrap(true);
        label.setAlignment(Align.center);
        add(label).growX();
        
        mode = Mode.GENERATING;
        label.setText("GENERATING PROJECT");
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (tenPatchDrawable.getRegions().peek().equals(tenPatchDrawable.getKeyFrame())) {
            switch (mode) {
                case SUCCESS:
                    mode = Mode.HIDING;
                    label.setText("DOWNLOADING");
                    
                    Gdx.net.openURI(backendClient.getDownloadUrl(generatorResponse.retrievedData));
                    
                    clearChildren();
                    tenPatchDrawable = new TenPatchDrawable(skin.get("loading-hide", TenPatchDrawable.class));
                    Image image = new Image(tenPatchDrawable);
                    add(image);
                    row();
                    add(label);
                    
                    break;
                case HIDING:
                    mode = Mode.DONE;
                    break;
            }
        }
    }
    
    private class WaitForResponseListener<T> implements BackendClient.IBackendResponse<T> {
        T retrievedData;
        
        WaitForResponseListener() {
            mode = Mode.GENERATING;
        }
        
        @Override
        public void onFail(int statusCode, String errorMsg) {
            mode = Mode.FAIL;
    
            Gdx.app.postRunnable(() -> {
                mode = Mode.FAIL;
                label.setText("CANNOT CONNECT TO SERVER\nERROR CODE " + statusCode);
            });
        }
        
        @Override
        public void onSuccess(T retrievedData) {
            this.retrievedData = retrievedData;
            
            Gdx.app.postRunnable(() -> {
                if (((GeneratorResponse) retrievedData).downloadUrl == null) {
                    mode = Mode.FAIL;
                    label.setText("FAILED TO GENERATE PROJECT\nERROR CODE " + ((GeneratorResponse) retrievedData).errorMessage);
                } else {
                    mode = Mode.SUCCESS;
                }
            });
            
        }
    }
}

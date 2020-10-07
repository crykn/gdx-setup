package com.badlogic.gdx.setup.tables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.setup.backend.BackendClient;
import com.ray3k.tenpatch.TenPatchDrawable;

import static com.badlogic.gdx.setup.SetupUi.backendClient;
import static com.badlogic.gdx.setup.SetupUi.skin;

public class GenerateLoadingTable extends Table  {
    WaitForResponseListener<BackendClient.GeneratorResponse> generatorResponse;
    TenPatchDrawable tenPatchDrawable;
    Mode mode;
    public enum Mode {
        GENERATING, SUCCESS, FAIL, HIDING, DONE
    }

    public GenerateLoadingTable(BackendClient.GenerateProjectParams params) {
        generatorResponse = new WaitForResponseListener<>();
        backendClient.generateProject(params, generatorResponse);
        
        tenPatchDrawable = new TenPatchDrawable(skin.get("loading-animation", TenPatchDrawable.class));
        Image image = new Image(tenPatchDrawable);
        add(image);
        mode = Mode.GENERATING;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (tenPatchDrawable.getRegions().peek().equals(tenPatchDrawable.getKeyFrame())) {
            switch (mode) {
                case SUCCESS:
                    mode = Mode.HIDING;
                    
                    Gdx.net.openURI(generatorResponse.retrievedData.getDownloadUrl());
                    
                    clearChildren();
                    tenPatchDrawable = new TenPatchDrawable(skin.get("loading-hide", TenPatchDrawable.class));
                    Image image = new Image(tenPatchDrawable);
                    add(image);
                    break;
                case HIDING:
                    clearChildren();
                    mode = Mode.DONE;
                    break;
            }
        }
    }
    
    private class WaitForResponseListener<T> implements BackendClient.IBackendResponse<T> {
        T retrievedData;
        int lastCode;
        
        WaitForResponseListener() {
            mode = Mode.GENERATING;
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

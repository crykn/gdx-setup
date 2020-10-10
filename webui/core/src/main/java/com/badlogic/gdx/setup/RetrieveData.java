package com.badlogic.gdx.setup;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.setup.backend.BackendClient;
import com.badlogic.gdx.setup.backend.VersionResponse;

import static com.badlogic.gdx.setup.SetupUi.*;

public class RetrieveData {
    WaitForResponseListener<VersionResponse> versionResponse;
    Mode mode;
    public enum Mode {
        REQUESTING, SUCCESS, FAIL
    }

    public RetrieveData() {
        versionResponse = new WaitForResponseListener<>();
        backendClient.getVersions(versionResponse);
        mode = Mode.REQUESTING;
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
            });
        }
        
        @Override
        public void onSuccess(T retrievedData) {
            this.retrievedData = retrievedData;
            Gdx.app.postRunnable(() -> {
                mode = Mode.SUCCESS;
                supportedGDXVersions = versionResponse.retrievedData.supportedGdxVersions;
                buildVersion = versionResponse.retrievedData.backendVersion;
            });
        }
    }
}

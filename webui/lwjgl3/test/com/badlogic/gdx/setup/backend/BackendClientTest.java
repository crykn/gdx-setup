package com.badlogic.gdx.setup.backend;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.graphics.GL20;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Created by Benjamin Schulte on 12.09.2018.
 */
public class BackendClientTest {
    boolean requesting = false;

    @BeforeClass
    public static void init() {
        // Note that we don't need to implement any of the listener's methods
        Gdx.app = new HeadlessApplication(new ApplicationListener() {
            @Override
            public void create() {
            }

            @Override
            public void resize(int width, int height) {
            }

            @Override
            public void render() {
            }

            @Override
            public void pause() {
            }

            @Override
            public void resume() {
            }

            @Override
            public void dispose() {
            }
        });

        // Use Mockito to mock the OpenGL methods since we are running headlessly
        Gdx.gl20 = Mockito.mock(GL20.class);
        Gdx.gl = Gdx.gl20;

        Gdx.app.setLogLevel(Application.LOG_DEBUG);
    }

    @Test
    public void testGetVersionInfo() throws InterruptedException {
        BackendClient backendClient = new BackendClient();
        WaitForResponseListener<BackendClient.VersionResponse> versionResponse
                = new WaitForResponseListener<>();
        backendClient.getVersions(versionResponse);
        waitWhileRequesting();
        Assert.assertNotNull(versionResponse.retrievedData);
        Assert.assertNotNull(versionResponse.retrievedData.supportedGdxVersions);
        Assert.assertFalse(versionResponse.retrievedData.supportedGdxVersions.isEmpty());
    }

    @Test
    public void testGenerateProject() throws InterruptedException {
        BackendClient backendClient = new BackendClient();
        BackendClient.GenerateProjectParams params = new BackendClient.GenerateProjectParams();
        params.appName = "test";
        params.gdxVersion = "1.9.11";
        params.mainClass = "MyTestClass";
        params.withHtml = true;

        WaitForResponseListener<BackendClient.GeneratorResponse> generatorResponse
                = new WaitForResponseListener<>();
        backendClient.generateProject(params, generatorResponse);
        waitWhileRequesting();
        Assert.assertNotNull(generatorResponse.retrievedData);
        Assert.assertNotNull(generatorResponse.retrievedData.warnings);
        if (generatorResponse.retrievedData.downloadUrl != null) {
            Assert.assertNotNull(generatorResponse.retrievedData.getDownloadUrl());
        }
    }

    @After
    public void waitWhileRequesting() throws InterruptedException {
        while (requesting) {
            Thread.sleep(100);
        }
    }

    private class WaitForResponseListener<T> implements BackendClient.IBackendResponse<T> {
        T retrievedData;
        boolean successful;
        int lastCode;

        WaitForResponseListener() {
            requesting = true;
        }

        @Override
        public void onFail(int statusCode, String errorMsg) {
            requesting = false;
            successful = false;
            lastCode = statusCode;
        }

        @Override
        public void onSuccess(T retrievedData) {
            this.retrievedData = retrievedData;
            successful = true;
            lastCode = 0;
            requesting = false;
        }
    }
}
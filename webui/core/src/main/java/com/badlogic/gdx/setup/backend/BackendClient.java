package com.badlogic.gdx.setup.backend;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpParametersUtils;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BackendClient {
    public static final int SC_NO_CONNECTION = 0;
    private static final String BASE_URL = "https://gdx-setup-backend.herokuapp.com";
    private static final String LOG_TAG = "BACKEND";

    public void generateProject(GenerateProjectParams params, IBackendResponse<GeneratorResponse> callback) {
        Map<String, String> httpParams = new HashMap<>();
        httpParams.put("appName", params.appName);
        httpParams.put("packageName", params.packageName);
        httpParams.put("mainClass", params.mainClass);
        httpParams.put("withAndroid", String.valueOf(params.withAndroid));
        httpParams.put("withHtml", String.valueOf(params.withHtml));
        httpParams.put("withDesktop", String.valueOf(params.withDesktop));
        httpParams.put("withIos", String.valueOf(params.withIos));
        httpParams.put("gdxVersion", params.gdxVersion);

        final Net.HttpRequest httpRequest = buildRequest("/generate", httpParams);
        httpRequest.setMethod(Net.HttpMethods.GET);

        Gdx.net.sendHttpRequest(httpRequest, new HttpResponseHandler<GeneratorResponse>(callback) {
            @Override
            GeneratorResponse parseJsonResponse(JsonValue json) {
                GeneratorResponse response = new GeneratorResponse();

                response.downloadUrl = json.getString("downloadUrl", null);
                response.errorMessage = json.getString("errorMessage", null);

                List<String> warnings = new ArrayList<>();
                for (JsonValue warning = json.get("warnings").child; warning != null; warning = warning.next) {
                    warnings.add(warning.asString());
                }
                response.warnings = warnings.toArray(new String[]{});

                return response;
            }
        });
    }

    public void getVersions(IBackendResponse<VersionResponse> callback) {
        final Net.HttpRequest httpRequest = buildRequest("/versions", null);
        httpRequest.setMethod(Net.HttpMethods.GET);

        Gdx.net.sendHttpRequest(httpRequest, new HttpResponseHandler<VersionResponse>(callback) {
            @Override
            VersionResponse parseJsonResponse(JsonValue json) {
                VersionResponse response = new VersionResponse();

                response.backendVersion = json.getString("backendVersion", null);

                List<String> gdxVersions = new ArrayList<>();
                for (JsonValue gdxVersion = json.get("supportedGdxVersions").child; gdxVersion != null; gdxVersion = gdxVersion.next) {
                    gdxVersions.add(gdxVersion.asString());
                }
                response.supportedGdxVersions = gdxVersions.toArray(new String[0]);

                List<String> extensions = new ArrayList<>();
                for (JsonValue extension = json.get("availableExtensions").child; extension != null; extension = extension.next) {
                    extensions.add(extension.asString());
                }
                response.availableExtensions = extensions.toArray(new String[0]);

                return response;
            }
        });
    }

    protected Net.HttpRequest buildRequest(String uri, Map<String, String> params) {
        if (!uri.startsWith("/"))
            uri = "/" + uri;

        final Net.HttpRequest http = new Net.HttpRequest();
        String paramString = params != null ? HttpParametersUtils.convertHttpParameters(params) : "";
        if (paramString.length() > 0)
            uri = uri + "?" + paramString;

        http.setUrl(BASE_URL + uri);
        Gdx.app.debug(LOG_TAG, uri);
        return http;
    }

    public String getDownloadUrl(GeneratorResponse generatorResponse) {
        return BASE_URL + "/download/" + generatorResponse.downloadUrl;
    }

    public interface IBackendResponse<T> {
        void onFail(int statusCode, String errorMsg);

        void onSuccess(T retrievedData);
    }

    private abstract static class HttpResponseHandler<T> implements Net.HttpResponseListener {
        private final IBackendResponse<T> callback;

        public HttpResponseHandler(IBackendResponse<T> callback) {
            this.callback = callback;
        }

        abstract T parseJsonResponse(JsonValue json);

        @Override
        public void handleHttpResponse(Net.HttpResponse httpResponse) {
            String result = httpResponse.getResultAsString();
            int statusCode = httpResponse.getStatus().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                try {
                    JsonValue response = new JsonReader().parse(result);
                    if (callback != null)
                        callback.onSuccess(parseJsonResponse(response));
                } catch (Throwable t) {
                    Gdx.app.log(LOG_TAG, "Could not parse answer: " + result, t);
                    if (callback != null)
                        callback.onFail(statusCode, "Server error.");
                }
            } else {
                Gdx.app.log(LOG_TAG, statusCode + ": " + result);
                if (callback != null) {
                    String errorMsg = result;
                    if (statusCode > 0 && (errorMsg == null || errorMsg.isEmpty()))
                        errorMsg = "Server returned error " + String.valueOf(statusCode);
                    else if (statusCode <= 0 && errorMsg == null) {
                        statusCode = SC_NO_CONNECTION;
                        errorMsg = "Connection problem";
                    }

                    callback.onFail(statusCode, errorMsg);
                }
            }
        }

        @Override
        public void failed(Throwable t) {
            Gdx.app.error(LOG_TAG, t.getMessage(), t);
            callback.onFail(SC_NO_CONNECTION, t.getMessage());
        }

        @Override
        public void cancelled() {
            callback.onFail(SC_NO_CONNECTION, "Connection problem");
        }
    }
}

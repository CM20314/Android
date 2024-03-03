package com.cm20314.mapapp.services;

import static com.microsoft.appcenter.utils.HandlerUtils.runOnUiThread;

import android.os.Handler;
import android.os.Looper;

import com.cm20314.mapapp.BuildConfig;
import com.cm20314.mapapp.interfaces.IHttpRequestCallback;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpRequestService<TIn, TOut> {

    public static CircularProgressIndicator progressIndicator;

    public static class HttpRequestResponse<T> {
        public T Content;
        public String ResponseBody;
        public int ResponseStatusCode;
        public boolean ConnectionSucceeded;
    }

    public static class SendRequestTask<TIn, TOut> implements Runnable {
        private final String method;
        private final String uri;
        private final TIn obj;
        private final boolean hasContent;
        private final Handler uiHandler;
        private final IHttpRequestCallback<TOut> callback;
        private final Class<TOut> outClass;

        public SendRequestTask(String method, String uri, TIn obj, boolean hasContent,
                               Handler uiHandler, IHttpRequestCallback<TOut> callback, Class<TOut> outClass) {
            this.method = method;
            this.uri = uri;
            this.obj = obj;
            this.hasContent = hasContent;
            this.uiHandler = uiHandler;
            this.callback = callback;
            this.outClass = outClass;
        }

        @Override
        public void run() {
            HttpRequestResponse<TOut> httpResponse = new HttpRequestResponse<>();
            if (progressIndicator != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressIndicator.show();
                    }
                });
            }

            try {
                URL url = new URL(uri);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod(method);

                connection.setRequestProperty("x-api-key", BuildConfig.API_KEY);

                if (hasContent) {
                    connection.setRequestProperty("Content-Type", "application/json");
                    String json = new Gson().toJson(obj);
                    byte[] outputInBytes = json.getBytes(StandardCharsets.UTF_8);
                    OutputStream os = connection.getOutputStream();
                    os.write(outputInBytes);
                    os.close();
                }

                int responseCode = connection.getResponseCode();
                String responseBody = readResponse(connection);
                if(hasContent){
                    //responseBody = Constants.defResponse;
                }

                httpResponse.ResponseStatusCode = responseCode;
                httpResponse.ResponseBody = responseBody;

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    httpResponse.Content = parseResponse(responseBody);
                    httpResponse.ConnectionSucceeded = true;
                }

            } catch (Exception e) {
                httpResponse.ConnectionSucceeded = false;
                handleException();
            }

                if (progressIndicator != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressIndicator.hide();
                        }
                    });
                }

            handleResponse(httpResponse);
        }

        private void handleResponse(final HttpRequestResponse<TOut> httpResponse) {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onCompleted(httpResponse);
                }
            });
        }

        private TOut parseResponse(String responseBody) {
            return new Gson().fromJson(responseBody, outClass);
        }

        private void handleException() {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onException();
                }
            });
        }

        private String readResponse(HttpURLConnection connection) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            return response.toString();
        }
    }

    public void sendHttpRequest(String method, String uri, TIn obj, boolean hasContent,
                                IHttpRequestCallback<TOut> callback, Class<TOut> outClass) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        // Provide the exact types for TIn and TOut when creating SendRequestTask
        SendRequestTask<TIn, TOut> sendRequestTask = new SendRequestTask<TIn, TOut>(
                method, uri, obj, hasContent, handler, callback, outClass
        );

        executor.execute(sendRequestTask);
    }
}

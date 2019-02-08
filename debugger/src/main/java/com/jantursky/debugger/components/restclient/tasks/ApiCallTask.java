package com.jantursky.debugger.components.restclient.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.jantursky.debugger.components.restclient.interfaces.RestClientResultListener;
import com.jantursky.debugger.components.restclient.models.ApiCallModel;
import com.jantursky.debugger.components.restclient.models.ApiGeneralModel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class ApiCallTask extends AsyncTask<Void, Void, ApiCallModel> {

    private static final String TAG = ApiCallTask.class.getSimpleName();

    private final ApiCallModel model;
    private final ApiGeneralModel apiCallGeneralModel;
    private RestClientResultListener listener;

    public ApiCallTask(ApiGeneralModel apiCallGeneralModel, ApiCallModel apiCallModel, RestClientResultListener listener) {
        this.apiCallGeneralModel = apiCallGeneralModel;
        this.model = apiCallModel;
        this.listener = listener;
    }

    @Override
    protected ApiCallModel doInBackground(Void... params) {
        HttpsURLConnection connection = null;
        try {
            model.startTime = System.currentTimeMillis();
            Log.w(TAG, "##### RUN API CALL " + model.type + " " + model.url);
            URL url = new URL(model.url);
            connection = (HttpsURLConnection) url.openConnection();

            connection.setReadTimeout(model.readTimeout);
            connection.setConnectTimeout(model.connectionTimeout);
//            Log.w(TAG, "##### RUN API CALL timeout: " + model.readTimeout + ", " + model.connectionTimeout);
//            connection.setUseCaches(false);

            if (apiCallGeneralModel != null && apiCallGeneralModel.hasHeaders()) {
                for (Map.Entry<String, String> entry : apiCallGeneralModel.headers.entrySet()) {
//                    Log.w(TAG, "##### RUN API CALL general headers: " + entry.getKey() + " = " + entry.getValue());
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            if (model.headers != null) {
                for (Map.Entry<String, String> entry : model.headers.entrySet()) {
//                    Log.w(TAG, "##### RUN API CALL headers: " + entry.getKey() + " = " + entry.getValue());
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }


            boolean canReadOutput = false;
            if (model.isPostType()) {
                canReadOutput = true;
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
            } else if (model.isGetType()) {
                canReadOutput = true;
//                connection.setDoInput(true);
//                connection.setDoOutput(true);
                connection.setRequestMethod("GET");
            } else if (model.isPutType()) {
                canReadOutput = true;
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestMethod("PUT");
            } else if (model.isDeleteType()) {
                canReadOutput = true;
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestMethod("DELETE");
            }

            if (model.hasInput()) {
                OutputStream os = connection.getOutputStream();
                os.write(model.body.getBytes("UTF-8"));
                os.flush();
                os.close();
            }
            connection.connect();

            int statusCode = connection.getResponseCode();
//            Log.w(TAG, "##### RUN API CALL STATUS CODE: " + statusCode);
            if (statusCode >= 200 && statusCode < 300) {
                if (canReadOutput) {
                    try {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        StringBuilder total = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            total.append(line).append('\n');
                        }
                        model.responseData = total.toString();
                        model.responseLength = total.length();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                if (canReadOutput) {
                    try {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                        StringBuilder total = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            total.append(line).append('\n');
                        }
                        model.responseError = total.toString();
                        model.responseLength = total.length();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            model.responseCode = statusCode;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        model.endTime = System.currentTimeMillis();
        return model;
    }

    @Override
    protected void onCancelled(ApiCallModel apiCallModel) {
        if (listener != null) {
            model.isRunning = false;
            listener.onApiCallResult(model);
        }
    }

    @Override
    protected void onPostExecute(ApiCallModel model) {
        if (listener != null) {
            model.isRunning = false;
            listener.onApiCallResult(model);
        }
    }
}

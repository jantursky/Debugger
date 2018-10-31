package com.jantursky.debugger.components.restclient.models;

import com.jantursky.debugger.components.restclient.RestClientComponent;
import com.jantursky.debugger.components.restclient.annotations.ApiCallType;

import java.util.HashMap;

public class ApiCallModel {

    @ApiCallType
    public int type;
    private int uniqueId = -1;
    public String url;
    public HashMap<String, String> headers;
    public String body;
    public int connectionTimeout;
    public int readTimeout;

    public int called = 0;

    public boolean isRunning;
    public int responseCode = -1;
    public String responseData;
    public String responseError;

    public int getId() {
        if (uniqueId == -1) {
            uniqueId = RestClientComponent.counter.incrementAndGet();
        }
        return uniqueId;
    }

    public String getTypeAsString() {
        if (isGetType()) {
            return "GET";
        } else if (isPostType()) {
            return "POST";
        } else if (isPutType()) {
            return "PUT";
        } else if (isDeleteType()) {
            return "DELETE";
        }
        return "";
    }

    public void startApiCall() {
        called++;
        isRunning = true;
        responseCode = -1;
        responseData = null;
        responseError = null;
    }

    public boolean hasResponseCode() {
        return responseCode != -1;
    }

    public boolean isPostType() {
        return type == ApiCallType.POST;
    }

    public boolean isGetType() {
        return type == ApiCallType.GET;
    }

    public boolean isPutType() {
        return type == ApiCallType.PUT;
    }

    public boolean isDeleteType() {
        return type == ApiCallType.DELETE;
    }

    public boolean hasInput() {
        return body != null && body.length() > 0;
    }

    public void stopApiCall() {
        isRunning = false;
        responseCode = -1;
        responseData = null;
        responseError = null;
    }
}

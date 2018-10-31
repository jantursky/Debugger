package com.jantursky.debugger.components.restclient.models;

import com.jantursky.debugger.components.restclient.annotations.ApiCallType;

import java.util.HashMap;

public class ApiCallGetModel extends ApiCallModel {

    public ApiCallGetModel(String url, HashMap<String, String> headers,
                           int connectionTimeout, int readTimeout) {
        this.type = ApiCallType.GET;
        this.url = url;
        this.headers = headers;
        this.connectionTimeout = connectionTimeout;
        this.readTimeout = readTimeout;
    }

    public static class Builder {

        private String url;
        public HashMap<String, String> headers;

        public int connectionTimeout = 10000;
        public int readTimeout = 10000;

        public Builder(String url) {
            this.url = url;
        }

        public Builder addHeader(String key, String value) {
            if (headers == null) {
                headers = new HashMap<>();
            }
            this.headers.put(key, value);
            return this;
        }

        public Builder addConnectionTimeout(int timeout) {
            this.connectionTimeout = timeout;
            return this;
        }

        public Builder addReadTimeout(int timeout) {
            this.readTimeout = timeout;
            return this;
        }

        public ApiCallGetModel build() {
            return new ApiCallGetModel(url, headers, connectionTimeout, readTimeout);
        }
    }
}

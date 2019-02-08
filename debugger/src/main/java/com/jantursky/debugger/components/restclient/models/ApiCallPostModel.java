package com.jantursky.debugger.components.restclient.models;

import com.jantursky.debugger.components.restclient.annotations.ApiCallType;

import org.json.JSONObject;

import java.util.LinkedHashMap;

public class ApiCallPostModel extends ApiCallModel {

    public ApiCallPostModel(String url, String body, LinkedHashMap<String, String> headers,
                            int connectionTimeout, int readTimeout) {
        this.type = ApiCallType.POST;
        this.url = url;
        this.body = body;
        this.headers = headers;
        this.connectionTimeout = connectionTimeout;
        this.readTimeout = readTimeout;
    }

    public static class Builder {

        private String url;
        private String body;
        public LinkedHashMap<String, String> headers;

        public int connectionTimeout = 10000;
        public int readTimeout = 10000;

        public Builder(String url) {
            this.url = url;
        }

        public Builder addHeader(String key, String value) {
            if (headers == null) {
                headers = new LinkedHashMap<>();
            }
            this.headers.put(key, value);
            return this;
        }

        public Builder addBody(String body) {
            this.body = body;
            return this;
        }

        public Builder addBody(JSONObject body) {
            this.body = body.toString();
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

        public ApiCallPostModel build() {
            return new ApiCallPostModel(url, body, headers, connectionTimeout, readTimeout);
        }
    }
}

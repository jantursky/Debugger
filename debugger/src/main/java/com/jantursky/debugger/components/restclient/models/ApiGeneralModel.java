package com.jantursky.debugger.components.restclient.models;

import java.util.LinkedHashMap;

public class ApiGeneralModel {

    public LinkedHashMap<String, String> headers;

    public void addHeader(String key, String value) {
        if (headers == null) {
            headers = new LinkedHashMap<>();
        }
        headers.put(key, value);
    }

    public void addHeader(String... values) {
        if (headers == null) {
            headers = new LinkedHashMap<>();
        }
        if (values.length % 2 == 0) {
            for (int i = 0; i < values.length; i = i + 2) {
                headers.put(values[i], values[i + 1]);
            }
        }
    }

    public boolean hasHeaders() {
        return headers != null && !headers.isEmpty();
    }

    public void setHeaders(LinkedHashMap<String, String> headers) {
        if (this.headers == null) {
            this.headers = new LinkedHashMap<>();
        } else {
            this.headers.clear();
        }
        this.headers.putAll(headers);
    }
}
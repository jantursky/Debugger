package com.jantursky.debugger.components.restclient.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.jantursky.debugger.R;
import com.jantursky.debugger.components.restclient.RestClientComponent;
import com.jantursky.debugger.components.restclient.annotations.ApiCallType;
import com.jantursky.debugger.utils.StringUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ApiCallModel implements Parcelable {

    @ApiCallType
    public int type;
    private int uniqueId = -1;
    public String url;
    public LinkedHashMap<String, String> headers;
    public String body;
    public int connectionTimeout;
    public int readTimeout;

    public int called = 0;

    public long startTime = -1, endTime = -1;
    public long responseLength = -1;
    public boolean isRunning;
    public int responseCode = -1;
    public String responseData;
    public String responseError;

    public ApiCallModel() {
    }

    public int getId() {
        if (uniqueId == -1) {
            uniqueId = RestClientComponent.counter.incrementAndGet();
        }
        return uniqueId;
    }

    public int getTypeAsString() {
        if (isGetType()) {
            return R.string.rest_type_get;
        } else if (isPostType()) {
            return R.string.rest_type_post;
        } else if (isPutType()) {
            return R.string.rest_type_put;
        } else if (isDeleteType()) {
            return R.string.rest_type_delete;
        }
        return 0;
    }

    public int getBgForType() {
        if (isGetType()) {
            return R.drawable.rest_get_drawable;
        } else if (isPostType()) {
            return R.drawable.rest_post_drawable;
        } else if (isPutType()) {
            return R.drawable.rest_put_drawable;
        } else if (isDeleteType()) {
            return R.drawable.rest_delete_drawable;
        }
        return 0;
    }

    public void startApiCall() {
        called++;
        isRunning = true;
        responseLength = -1;
        responseCode = -1;
        startTime = -1;
        endTime = -1;
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
        responseLength = -1;
        startTime = -1;
        endTime = -1;
        responseData = null;
        responseError = null;
    }

    protected ApiCallModel(Parcel in) {
        type = in.readInt();
        uniqueId = in.readInt();
        url = in.readString();
        headers = (LinkedHashMap) in.readValue(HashMap.class.getClassLoader());
        body = in.readString();
        connectionTimeout = in.readInt();
        readTimeout = in.readInt();
        called = in.readInt();
        isRunning = in.readByte() != 0x00;
        responseCode = in.readInt();
        responseData = in.readString();
        responseError = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeInt(uniqueId);
        dest.writeString(url);
        dest.writeValue(headers);
        dest.writeString(body);
        dest.writeInt(connectionTimeout);
        dest.writeInt(readTimeout);
        dest.writeInt(called);
        dest.writeByte((byte) (isRunning ? 0x01 : 0x00));
        dest.writeInt(responseCode);
        dest.writeString(responseData);
        dest.writeString(responseError);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ApiCallModel> CREATOR = new Parcelable.Creator<ApiCallModel>() {
        @Override
        public ApiCallModel createFromParcel(Parcel in) {
            return new ApiCallModel(in);
        }

        @Override
        public ApiCallModel[] newArray(int size) {
            return new ApiCallModel[size];
        }
    };

    public String getHeadersAsString() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            if (builder.length() > 0) {
                builder.append("\r\n");
            }
            builder.append(entry.getKey());
            builder.append(" - ");
            builder.append(entry.getValue());
        }

        return builder.toString();
    }

    public boolean hasHeaders() {
        return headers != null && !headers.isEmpty();
    }

    public boolean hasResponseLength() {
        return responseLength >= 0;
    }

    public boolean hasResponseOrErrorData() {
        return hasResponseData() || hasErrorResponseData();
    }

    public boolean hasResponseData() {
        return !StringUtils.isEmpty(responseData);
    }

    public boolean hasErrorResponseData() {
        return !StringUtils.isEmpty(responseError);
    }

    public boolean hasEndTime() {
        return startTime > 0 && endTime > 0;
    }

    public String getResponseOrErrorData() {
        if (hasResponseData()) {
            return responseData;
        }
        return responseError;
    }
}
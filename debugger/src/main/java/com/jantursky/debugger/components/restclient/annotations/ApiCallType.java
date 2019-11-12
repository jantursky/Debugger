package com.jantursky.debugger.components.restclient.annotations;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef(value = {
        ApiCallType.GET, ApiCallType.POST, ApiCallType.PUT, ApiCallType.DELETE})
@Retention(RetentionPolicy.SOURCE)
public @interface ApiCallType {

    int GET = 0;
    int POST = 1;
    int PUT = 2;
    int DELETE = 3;

}

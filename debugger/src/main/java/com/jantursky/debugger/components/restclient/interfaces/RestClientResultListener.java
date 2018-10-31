package com.jantursky.debugger.components.restclient.interfaces;

import com.jantursky.debugger.components.restclient.models.ApiCallModel;

public interface RestClientResultListener {

    void onApiCallResult(ApiCallModel model);

    void onApiCallCancel(ApiCallModel model);

}

package com.jantursky.debugger.components.restclient.interfaces;

import com.jantursky.debugger.components.restclient.models.ApiCallModel;

public interface RestClientListItemListener {

    void onRowClick(ApiCallModel model);

    void runCall(ApiCallModel model);

    void stopCall(ApiCallModel model);

}

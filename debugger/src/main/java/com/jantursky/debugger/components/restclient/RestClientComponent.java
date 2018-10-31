package com.jantursky.debugger.components.restclient;

import android.content.Context;
import android.view.View;

import com.jantursky.debugger.annotations.DebuggerComponentType;
import com.jantursky.debugger.components.DebuggerComponent;
import com.jantursky.debugger.components.restclient.models.ApiCallModel;
import com.jantursky.debugger.components.sharedpreferencesviewer.SharedPreferencesView;
import com.jantursky.debugger.interfaces.ComponentListener;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class RestClientComponent extends DebuggerComponent {

    private static final String TAG = RestClientComponent.class.getSimpleName();

    public final static AtomicInteger counter = new AtomicInteger(0);
    private final ArrayList<ApiCallModel> apiCalls;

    public RestClientComponent(ArrayList<ApiCallModel> apiCallModels) {
        super(DebuggerComponentType.REST_CLIENT);
        this.apiCalls = apiCallModels;
    }

    @Override
    public View createView(Context context, ComponentListener listener) {
        RestClientView view = new RestClientView(context);
        view.setComponentListener(listener);
        view.setApiCalls(apiCalls);
        return view;
    }
}
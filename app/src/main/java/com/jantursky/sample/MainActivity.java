package com.jantursky.sample;

import android.app.Activity;
import android.os.Bundle;

import com.jantursky.debugger.components.DebuggerComponentConstructor;
import com.jantursky.debugger.components.dbviewer.DbViewerComponent;
import com.jantursky.debugger.components.restclient.RestClientComponent;
import com.jantursky.debugger.components.restclient.models.ApiCallDeleteModel;
import com.jantursky.debugger.components.restclient.models.ApiCallGetModel;
import com.jantursky.debugger.components.restclient.models.ApiCallModel;
import com.jantursky.debugger.components.restclient.models.ApiCallPostModel;
import com.jantursky.debugger.components.restclient.models.ApiCallPutModel;
import com.jantursky.debugger.components.restclient.models.ApiGeneralModel;
import com.jantursky.debugger.components.sharedpreferencesviewer.SharedPreferencesViewerComponent;

import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DebuggerComponentConstructor builder = DebuggerComponentConstructor
                .builder()
                .add(new DbViewerComponent())
                .add(new SharedPreferencesViewerComponent())
                .add(new RestClientComponent(generateGeneralApiCall(), generateApiCalls()))
                .build(this, R.id.root_layout);
    }

    private ApiGeneralModel generateGeneralApiCall() {
        ApiGeneralModel apiGeneralModel = new ApiGeneralModel();
        apiGeneralModel.addHeader("Accept", "application/json");
        apiGeneralModel.addHeader("Content-Type", "application/json");
        return apiGeneralModel;
    }

    private ArrayList<ApiCallModel> generateApiCalls() {
        ArrayList<ApiCallModel> apiCall = new ArrayList<>();
        apiCall.add(new ApiCallGetModel
                .Builder("https://jsonplaceholder.typicode.com/posts")
                .build());
        apiCall.add(new ApiCallPostModel
                .Builder("https://jsonplaceholder.typicode.com/posts")
                .build());
        apiCall.add(new ApiCallPutModel
                .Builder("https://jsonplaceholder.typicode.com/posts/1")
                .addBody(generateBody())
                .build());
        apiCall.add(new ApiCallDeleteModel
                .Builder("https://jsonplaceholder.typicode.com/posts/1")
                .build());
        return apiCall;
    }

    private JSONObject generateBody() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", 1);
            jsonObject.put("title", "foo");
            jsonObject.put("body", "bar");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}

package com.jantursky.debugger.components.restclient;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jantursky.debugger.R;
import com.jantursky.debugger.components.restclient.adapters.RestClientListAdapter;
import com.jantursky.debugger.components.restclient.interfaces.RestClientListItemListener;
import com.jantursky.debugger.components.restclient.interfaces.RestClientResultListener;
import com.jantursky.debugger.components.restclient.models.ApiCallModel;
import com.jantursky.debugger.components.restclient.tasks.ApiCallTask;
import com.jantursky.debugger.interfaces.ComponentListener;
import com.jantursky.debugger.utils.RecyclerViewUtils;

import java.util.ArrayList;

public class RestClientView extends FrameLayout implements View.OnClickListener, RestClientListItemListener, RestClientResultListener {

    private static final String TAG = RestClientView.class.getSimpleName();

    private TextView txtRunAll, txtStopAll, txtClose;

    private RestClientListAdapter restClientListAdapter;

    private RecyclerView recyclerViewList;

    private ComponentListener componentListener;

    private SparseArray<AsyncTask> tasks = new SparseArray<>();

    public RestClientView(@NonNull Context context) {
        super(context);
        init();
    }

    public RestClientView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RestClientView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RestClientView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_rest_client, this, true);

        recyclerViewList = view.findViewById(R.id.list_recycler_view);

        txtClose = view.findViewById(R.id.close_textview);
        txtRunAll = view.findViewById(R.id.run_all_textview);
        txtStopAll = view.findViewById(R.id.stop_all_textview);

        setList();
        setListeners();
    }

    public void setComponentListener(ComponentListener componentListener) {
        this.componentListener = componentListener;
    }

    private void setList() {
        restClientListAdapter = new RestClientListAdapter(getContext());
        recyclerViewList.addItemDecoration(RecyclerViewUtils.getVerticalItemDecoration(getContext()));
        recyclerViewList.setAdapter(restClientListAdapter);
        ((SimpleItemAnimator) recyclerViewList.getItemAnimator()).setSupportsChangeAnimations(false);
        restClientListAdapter.setOnItemClickListener(this);
    }

    private void setListeners() {
        txtRunAll.setOnClickListener(this);
        txtStopAll.setOnClickListener(this);
        txtClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.close_textview) {
            stopAllCalls();
            if (componentListener != null) {
                componentListener.closeComponent();
            }
        } else if (viewId == R.id.run_all_textview) {
            if (isNetworkAvailable(getContext())) {
                runAllCalls();
            } else {
                Toast.makeText(getContext(), R.string.toast_no_network, Toast.LENGTH_SHORT).show();
            }
        } else if (viewId == R.id.stop_all_textview) {
            stopAllCalls();
        }
    }

    private void runAllCalls() {
        ArrayList<ApiCallModel> data = restClientListAdapter.getData();
        for (ApiCallModel model : data) {
            runCall(model);
        }
    }

    private void stopAllCalls() {
        ArrayList<ApiCallModel> data = restClientListAdapter.getData();
        for (ApiCallModel model : data) {
            stopCall(model);
        }
    }

    @Override
    public void onRowClick(ApiCallModel model) {

    }

    @Override
    public void runCall(ApiCallModel model) {
        if (isNetworkAvailable(getContext())) {
            if (isRunning(model)) {
                cancel(model);
            }
            restClientListAdapter.runApiCall(model);
            tasks.put(model.getId(), new ApiCallTask(model, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR));
        } else {
            Toast.makeText(getContext(), R.string.toast_no_network, Toast.LENGTH_SHORT).show();
        }
    }

    private void cancel(ApiCallModel model) {
        if (tasks.get(model.getId()) != null) {
            closeTask(tasks.get(model.getId()));
            tasks.remove(model.getId());
            restClientListAdapter.cancelApiCall(model);
        }
    }

    private boolean isRunning(ApiCallModel model) {
        if (tasks.get(model.getId()) != null) {
            if (isRunningTask(tasks.get(model.getId()))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void stopCall(ApiCallModel model) {
        cancel(model);
    }

    public void setApiCalls(ArrayList<ApiCallModel> apiCalls) {
        this.restClientListAdapter.setData(apiCalls);
    }

    public static void closeTask(AsyncTask... tasks) {
        for (AsyncTask task : tasks) {
            if (isRunningTask(task)) {
                task.cancel(true);
            }
        }
    }

    public static boolean isRunningTask(AsyncTask task) {
        return task != null &&
                (task.getStatus() == AsyncTask.Status.PENDING ||
                        task.getStatus() == AsyncTask.Status.RUNNING);
    }

    @Override
    public void onApiCallResult(ApiCallModel model) {
        restClientListAdapter.updateApiCallResult(model);
    }

    @Override
    public void onApiCallCancel(ApiCallModel model) {
        restClientListAdapter.updateApiCallResult(model);
    }

    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = null;
            if (connectivityManager != null) {
                activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            }
            return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
        } catch (Exception e) {
            return false;
        }
    }
}
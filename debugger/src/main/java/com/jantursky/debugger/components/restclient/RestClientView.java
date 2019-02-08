package com.jantursky.debugger.components.restclient;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.jantursky.debugger.R;
import com.jantursky.debugger.components.restclient.adapters.RestClientListAdapter;
import com.jantursky.debugger.components.restclient.interfaces.RestClientListItemListener;
import com.jantursky.debugger.components.restclient.interfaces.RestClientResultListener;
import com.jantursky.debugger.components.restclient.models.ApiCallModel;
import com.jantursky.debugger.components.restclient.models.ApiGeneralModel;
import com.jantursky.debugger.components.restclient.tasks.ApiCallTask;
import com.jantursky.debugger.interfaces.ComponentListener;
import com.jantursky.debugger.utils.JsonUtils;
import com.jantursky.debugger.utils.RecyclerViewUtils;
import com.jantursky.debugger.utils.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class RestClientView extends FrameLayout implements View.OnClickListener, RestClientListItemListener, RestClientResultListener {

    private static final String TAG = RestClientView.class.getSimpleName();

    private TextView txtRunAll, txtClose, txtGlobalHeaders;

    private RestClientListAdapter restClientListAdapter;

    private RecyclerView recyclerViewList;

    private ComponentListener componentListener;

    private ApiGeneralModel apiCallGeneralModel;
    private SparseArray<ApiCallTask> tasks = new SparseArray<>();

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
        txtGlobalHeaders = view.findViewById(R.id.global_headers_textview);

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
        txtGlobalHeaders.setOnClickListener(this);
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
            if (hasRunningTask()) {
                stopAllCalls();
            } else {
                if (isNetworkAvailable(getContext())) {
                    runAllCalls();
                } else {
                    Toast.makeText(getContext(), R.string.toast_no_network, Toast.LENGTH_SHORT).show();
                }
            }
            updateStatus();
        } else if (viewId == R.id.global_headers_textview) {
            editGlobalHeaders();
        }
    }

    private void editGlobalHeaders() {
        LayoutInflater li = LayoutInflater.from(getContext());
        View promptsView = li.inflate(R.layout.dialog_rest_headers, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setView(promptsView);

        final LinearLayout ltContent = promptsView.findViewById(R.id.content_layout);
        TextView txtAdd = promptsView.findViewById(R.id.add_textview);
        TextView txtCancel = promptsView.findViewById(R.id.cancel_textview);
        TextView txtSave = promptsView.findViewById(R.id.save_textview);

        if (apiCallGeneralModel.hasHeaders()) {
            for (Map.Entry<String, String> entry : apiCallGeneralModel.headers.entrySet()) {
                addHeaderRow(ltContent, entry.getKey(), entry.getValue());
            }
        }

        alertDialogBuilder.setCancelable(false);

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        txtCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        txtSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LinkedHashMap<String, String> headers = new LinkedHashMap<>();
                for (int i = 0; i < ltContent.getChildCount(); i++) {
                    EditText editTextKey = ltContent.getChildAt(i).findViewById(R.id.input_key_edittext);
                    EditText editTextValue = ltContent.getChildAt(i).findViewById(R.id.input_value_edittext);
                    String key = editTextKey.getText().toString();
                    String value = editTextValue.getText().toString();
                    headers.put(key, value);
                }
                apiCallGeneralModel.setHeaders(headers);
                restClientListAdapter.updateApiGeneralMode(apiCallGeneralModel);
                alertDialog.dismiss();
            }
        });

        txtAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addHeaderRow(ltContent, "", "");
            }
        });
    }

    private void updateStatus() {
        if (hasRunningTask()) {
            txtRunAll.setText(R.string.action_stop_all_api_call);
        } else {
            txtRunAll.setText(R.string.action_run_all_api_call);
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

            ApiCallTask task = new ApiCallTask(apiCallGeneralModel, model, this);
            tasks.put(model.getId(), task);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            Toast.makeText(getContext(), R.string.toast_no_network, Toast.LENGTH_SHORT).show();
        }
        updateStatus();
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

    @Override
    public void editHeaders(ApiCallModel model) {

    }

    private void addHeaderRow(LinearLayout layout, String key, String value) {
        LayoutInflater li = LayoutInflater.from(getContext());
        View view = li.inflate(R.layout.item_rest_header_row, null);
        EditText inputKeyEdittext = view.findViewById(R.id.input_key_edittext);
        EditText inputValueEdittext = view.findViewById(R.id.input_value_edittext);
        TextView txtDelete = view.findViewById(R.id.delete_textview);

        if (!StringUtils.isEmpty(key)) {
            inputKeyEdittext.setText(key);
        }
        if (!StringUtils.isEmpty(value)) {
            inputValueEdittext.setText(value);
        }

        txtDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ((LinearLayout) view.getParent().getParent()).removeView((View) view.getParent());
            }
        });

        layout.addView(view);
    }

    @Override
    public void displayOutputDetail(ApiCallModel model) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(model.hasResponseData() ? R.string.rest_output : R.string.rest_error_output);

        ScrollView scrollView = new ScrollView(getContext());
        TextView input = new TextView(getContext());
        input.setText(JsonUtils.getIndentedText(model.getResponseOrErrorData()));
        input.setPadding(32, 32, 32, 32);
        input.setSingleLine(false);

        scrollView.addView(input);
        builder.setView(scrollView);

        builder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void setApiCalls(ApiGeneralModel apiCallGeneralModel, ArrayList<ApiCallModel> apiCalls) {
        this.apiCallGeneralModel = apiCallGeneralModel;
        this.restClientListAdapter.setData(apiCallGeneralModel, apiCalls);
    }

    public boolean hasRunningTask() {
        return tasks.size() > 0;
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
        removeTask(model);
        updateStatus();
    }

    @Override
    public void onApiCallCancel(ApiCallModel model) {
        restClientListAdapter.updateApiCallResult(model);
        removeTask(model);
        updateStatus();
    }

    private void removeTask(ApiCallModel model) {
        if (tasks.get(model.getId()) != null) {
            tasks.remove(model.getId());
        }
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
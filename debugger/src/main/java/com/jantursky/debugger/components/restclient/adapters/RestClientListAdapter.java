package com.jantursky.debugger.components.restclient.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jantursky.debugger.R;
import com.jantursky.debugger.components.restclient.interfaces.RestClientListItemListener;
import com.jantursky.debugger.components.restclient.models.ApiCallModel;
import com.jantursky.debugger.components.restclient.models.ApiGeneralModel;
import com.jantursky.debugger.utils.AnnotationUtils;
import com.jantursky.debugger.utils.SizeUtils;
import com.jantursky.debugger.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Map;

public class RestClientListAdapter extends RecyclerView.Adapter<RestClientListAdapter.ItemHolder> {

    private final ArrayList<ApiCallModel> array;
    private final Context context;

    private RestClientListItemListener listener;
    private ApiGeneralModel apiCallGeneralModel;

    public RestClientListAdapter(Context context) {
        this.context = context;
        this.array = new ArrayList<>();
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return array.get(position).getId();
    }

    public void setData(ApiGeneralModel apiCallGeneralModel, ArrayList<ApiCallModel> data) {
        this.apiCallGeneralModel = apiCallGeneralModel;
        if (data != null && !data.isEmpty()) {
            array.clear();
            array.addAll(data);
        }
        notifyDataSetChanged();
    }

    public void updateApiGeneralMode(ApiGeneralModel apiCallGeneralModel) {
        this.apiCallGeneralModel = apiCallGeneralModel;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(RestClientListItemListener listener) {
        this.listener = listener;
    }

    public void cancelApiCall(ApiCallModel model) {
        if (hasData()) {
            for (int i = 0; i < array.size(); i++) {
                ApiCallModel apiCallModel = array.get(i);
                if (model.getId() == apiCallModel.getId()) {
                    array.get(i).stopApiCall();
                    notifyItemChanged(i);
                    break;
                }
            }
        }
    }

    public void runApiCall(ApiCallModel model) {
        if (hasData()) {
            for (int i = 0; i < array.size(); i++) {
                ApiCallModel apiCallModel = array.get(i);
                if (model.getId() == apiCallModel.getId()) {
                    array.get(i).startApiCall();
                    notifyItemChanged(i);
                    break;
                }
            }
        }
    }

    public void updateApiCallResult(ApiCallModel model) {
        if (hasData()) {
            for (int i = 0; i < array.size(); i++) {
                ApiCallModel apiCallModel = array.get(i);
                if (model.getId() == apiCallModel.getId()) {
                    array.set(i, model);
                    notifyItemChanged(i);
                    break;
                }
            }
        }
    }

    private boolean hasData() {
        return getItemCount() > 0;
    }

    public ArrayList<ApiCallModel> getData() {
        return array;
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        protected final View ltRoot, ltResponseOutput;
        protected final TextView txtUrl, txtType, txtCheck, txtRunCall, txtResponseCode,
                txtHeaders, txtResponseLength, txtResponseTime;
        protected final ProgressBar progressBar;

        public ItemHolder(View view) {
            super(view);

            ltRoot = view.findViewById(R.id.root_layout);
            ltResponseOutput = view.findViewById(R.id.response_output_layout);

            txtUrl = view.findViewById(R.id.url_textview);
            txtType = view.findViewById(R.id.type_textview);
            txtCheck = view.findViewById(R.id.check_textview);
            txtRunCall = view.findViewById(R.id.run_call_textview);
            txtResponseCode = view.findViewById(R.id.response_code_textview);
            txtResponseLength = view.findViewById(R.id.response_length_textview);
            txtResponseTime = view.findViewById(R.id.response_time_textview);

            txtHeaders = view.findViewById(R.id.response_headers_textview);

            progressBar = view.findViewById(R.id.progress_bar);

            txtRunCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ApiCallModel model = array.get(getAdapterPosition());
                    if (listener != null) {
                        if (model.isRunning) {
                            listener.stopCall(model);
                        } else {
                            listener.runCall(model);
                        }
                    }
                }
            });

            ltResponseOutput.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ApiCallModel model = array.get(getAdapterPosition());
                    if (listener != null) {
                        listener.displayOutputDetail(model);
                    }
                }
            });

            txtHeaders.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ApiCallModel model = array.get(getAdapterPosition());
                    if (listener != null) {
                        listener.editHeaders(model);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_rest_client_list,
                viewGroup, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemHolder holder, int position) {
        ApiCallModel model = array.get(position);

        holder.txtUrl.setText(model.url);

        holder.txtType.setText(model.getTypeAsString());
        holder.txtType.setBackgroundResource(model.getBgForType());

        if (model.isRunning) {
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.txtCheck.setVisibility(View.GONE);
        } else if (model.hasResponseCode()) {
            holder.progressBar.setVisibility(View.GONE);
            holder.txtCheck.setVisibility(View.VISIBLE);
        } else {
            holder.progressBar.setVisibility(View.GONE);
            holder.txtCheck.setVisibility(View.GONE);
        }

        if (model.isRunning) {
            holder.txtRunCall.setBackgroundResource(R.drawable.btn_red_drawable);
            holder.txtRunCall.setText(R.string.action_stop_api_call);
        } else {
            holder.txtRunCall.setBackgroundResource(R.drawable.btn_yellow_drawable);
            holder.txtRunCall.setText(R.string.action_run_api_call);
        }

        if (model.hasResponseCode()) {
            holder.txtResponseCode.setVisibility(View.VISIBLE);
            int responseTextId = context.getResources().getIdentifier("response_code_" + model.responseCode, "string", context.getPackageName());
            String responseText = "";
            if (responseTextId != 0) {
                responseText = context.getString(responseTextId);
            }
            holder.txtResponseCode.setText(context.getString(R.string.response_code, model.responseCode, responseText));
            holder.txtResponseCode.setTextColor(ContextCompat.getColor(context, AnnotationUtils.getTextColorFromResponseCode(model.responseCode)));
        } else {
            holder.txtResponseCode.setVisibility(View.GONE);
        }

        if (model.hasResponseLength()) {
            holder.txtResponseLength.setVisibility(View.VISIBLE);
            holder.txtResponseLength.setText(context.getString(R.string.response_length, SizeUtils.getDataSize(model.responseLength)));
        } else {
            holder.txtResponseLength.setVisibility(View.GONE);
        }

        if (model.hasEndTime()) {
            holder.txtResponseTime.setVisibility(View.VISIBLE);
            holder.txtResponseTime.setText(context.getString(R.string.response_time, TimeUtils.getLength(model.endTime - model.startTime)));
        } else {
            holder.txtResponseTime.setVisibility(View.GONE);
        }

        if (hasHeaders(model)) {
            holder.txtHeaders.setVisibility(View.VISIBLE);
            holder.txtHeaders.setText(getHeadersAsString(model));
        } else {
            holder.txtHeaders.setVisibility(View.GONE);
        }

        if (model.hasResponseOrErrorData()) {
            holder.ltResponseOutput.setVisibility(View.VISIBLE);
        } else {
            holder.ltResponseOutput.setVisibility(View.GONE);
        }
    }

    private String getHeadersAsString(ApiCallModel model) {
        StringBuilder builder = new StringBuilder();
        if (apiCallGeneralModel != null && apiCallGeneralModel.hasHeaders()) {
            for (Map.Entry<String, String> entry : apiCallGeneralModel.headers.entrySet()) {
                if (builder.length() > 0) {
                    builder.append("\r\n");
                }
                builder.append(entry.getKey());
                builder.append(" - ");
                builder.append(entry.getValue());
            }
        }

        if (model.hasHeaders()) {
            for (Map.Entry<String, String> entry : model.headers.entrySet()) {
                if (builder.length() > 0) {
                    builder.append("\r\n");
                }
                builder.append(entry.getKey());
                builder.append(" - ");
                builder.append(entry.getValue());
            }
        }

        return builder.toString();
    }

    private boolean hasHeaders(ApiCallModel model) {
        return model.hasHeaders() || (apiCallGeneralModel != null && apiCallGeneralModel.hasHeaders());
    }
}

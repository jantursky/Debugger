package com.jantursky.debugger.components.restclient.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jantursky.debugger.R;
import com.jantursky.debugger.components.restclient.interfaces.RestClientListItemListener;
import com.jantursky.debugger.components.restclient.models.ApiCallModel;

import java.util.ArrayList;

public class RestClientListAdapter extends RecyclerView.Adapter<RestClientListAdapter.ItemHolder> {

    private final ArrayList<ApiCallModel> array;
    private final Context context;

    private RestClientListItemListener listener;

    public RestClientListAdapter(Context context) {
        this.context = context;
        this.array = new ArrayList<>();
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return array.get(position).getId();
    }

    public void setData(ArrayList<ApiCallModel> data) {
        if (data != null && !data.isEmpty()) {
            array.clear();
            array.addAll(data);
        }
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

        protected final View ltRoot;
        protected final TextView txtUrl, txtType, txtRunCall, txtResponseCode;
        protected final ProgressBar progressBar;

        public ItemHolder(View view) {
            super(view);

            ltRoot = view.findViewById(R.id.root_layout);

            txtUrl = view.findViewById(R.id.url_textview);
            txtType = view.findViewById(R.id.type_textview);
            txtRunCall = view.findViewById(R.id.run_call_textview);
            txtResponseCode = view.findViewById(R.id.response_code_textview);

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

        holder.progressBar.setVisibility(model.isRunning ? View.VISIBLE : View.GONE);

        if (model.isRunning) {
            holder.txtRunCall.setBackgroundResource(R.drawable.btn_red_drawable);
            holder.txtRunCall.setText(R.string.action_stop_api_call);
        } else {
            holder.txtRunCall.setBackgroundResource(R.drawable.btn_yellow_drawable);
            holder.txtRunCall.setText(R.string.action_run_api_call);
        }

        if (model.hasResponseCode()) {
            holder.txtResponseCode.setVisibility(View.VISIBLE);
            holder.txtResponseCode.setText(context.getString(R.string.response_code, model.responseCode));
        } else {
            holder.txtResponseCode.setVisibility(View.GONE);
        }
    }
}
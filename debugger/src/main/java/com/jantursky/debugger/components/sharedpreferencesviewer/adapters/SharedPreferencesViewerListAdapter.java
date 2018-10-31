package com.jantursky.debugger.components.sharedpreferencesviewer.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jantursky.debugger.R;
import com.jantursky.debugger.components.sharedpreferencesviewer.interfaces.SharedPreferencesViewerListItemListener;
import com.jantursky.debugger.components.sharedpreferencesviewer.models.SharedPreferencesListModel;

import java.util.ArrayList;

public class SharedPreferencesViewerListAdapter extends RecyclerView.Adapter<SharedPreferencesViewerListAdapter.ItemHolder> {

    private final ArrayList<SharedPreferencesListModel> array;

    private SharedPreferencesViewerListItemListener listener;

    public SharedPreferencesViewerListAdapter() {
        this.array = new ArrayList<>();
    }

    public void setData(ArrayList<SharedPreferencesListModel> data) {
        if (data != null && !data.isEmpty()) {
            array.clear();
            array.addAll(data);
        }
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(SharedPreferencesViewerListItemListener listener) {
        this.listener = listener;
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        protected final View ltRoot;
        protected final TextView txtName;

        public ItemHolder(View view) {
            super(view);

            ltRoot = view.findViewById(R.id.ltRoot);
            txtName = view.findViewById(R.id.name_textview);
        }
    }

    @Override
    public int getItemCount() {
        return (null != array ? (array.size()) : 0);
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_shared_preferences_viewer_list,
                viewGroup, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemHolder holder, int position) {
        SharedPreferencesListModel model = array.get(position);

        holder.txtName.setText(model.file.getName());

        holder.ltRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAdapterPosition();
                if (pos != -1) {
                    if (listener != null) {
                        SharedPreferencesListModel model = array.get(pos);
                        listener.onFileClick(model.file);
                    }
                }
            }
        });
    }
}

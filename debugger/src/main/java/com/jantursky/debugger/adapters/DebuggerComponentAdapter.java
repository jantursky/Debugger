package com.jantursky.debugger.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jantursky.debugger.R;
import com.jantursky.debugger.components.DebuggerComponent;
import com.jantursky.debugger.components.dbviewer.DbViewerComponent;
import com.jantursky.debugger.listeners.DebuggerComponentItemListener;
import com.jantursky.debugger.utils.AnnotationUtils;

import java.util.ArrayList;

public class DebuggerComponentAdapter extends RecyclerView.Adapter<DebuggerComponentAdapter.ItemHolder> {

    private ArrayList<DebuggerComponent> array;

    private DebuggerComponentItemListener listener;

    public DebuggerComponentAdapter(ArrayList<DebuggerComponent> data) {
        this.array = data;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).componentType;
    }

    public void setOnItemClickListener(DebuggerComponentItemListener listener) {
        this.listener = listener;
    }


    public DebuggerComponent getItem(int pos) {
        return array.get(pos);
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        protected ViewGroup ltRoot;
        protected TextView txtName;

        public ItemHolder(View view, int viewType) {
            super(view);

            ltRoot = view.findViewById(R.id.root_layout);
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
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_debugger_component_row, viewGroup, false);
        return new ItemHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemHolder holder, int position) {
        DebuggerComponent model = getItem(position);

        holder.txtName.setText(AnnotationUtils.getTextIdForComponentType(model.componentType));

        holder.ltRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAdapterPosition();
                if (pos != -1) {
                    if (listener != null) {
                        listener.onItemSelect(getItem(pos));
                    }
                }
            }
        });
    }
}

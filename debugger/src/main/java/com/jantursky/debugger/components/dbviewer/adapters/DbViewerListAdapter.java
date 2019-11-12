package com.jantursky.debugger.components.dbviewer.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jantursky.debugger.R;
import com.jantursky.debugger.components.dbviewer.listeners.DbViewerListItemListener;
import com.jantursky.debugger.components.dbviewer.models.DbViewerModel;

import java.io.File;
import java.util.ArrayList;

public class DbViewerListAdapter extends RecyclerView.Adapter<DbViewerListAdapter.ItemHolder> {

    private final ArrayList<DbViewerModel> array;

    private DbViewerListItemListener listener;

    public DbViewerListAdapter() {
        this.array = new ArrayList<>();
    }

    public void setDatabases(ArrayList<File> databases) {
        if (databases != null && !databases.isEmpty()) {
            array.clear();

            for (File database : databases) {
                array.add(new DbViewerModel(database));
            }
        }
        notifyDataSetChanged();
    }

    public void setTables(ArrayList<String> tables) {
        if (tables != null && !tables.isEmpty()) {
            array.clear();

            array.add(new DbViewerModel(true));
            for (String table : tables) {
                array.add(new DbViewerModel(table));
            }
        }
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(DbViewerListItemListener listener) {
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
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_db_viewer_tables_list,
                viewGroup, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemHolder holder, int position) {
        DbViewerModel model = array.get(position);

        if (model.isJumpToRootType()) {
            holder.txtName.setText("...");
        } else if (model.isTableType()) {
            holder.txtName.setText(model.getTableName());
        } else if (model.isColumnType()) {
            holder.txtName.setText(model.getColumnName());
        }

        holder.ltRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAdapterPosition();
                if (pos != -1) {
                    if (listener != null) {
                        DbViewerModel model = array.get(pos);
                        if (model.isJumpToRootType()) {
                            listener.goBack();
                        } else if (model.isTableType()) {
                            listener.onDatabaseClick(model.table);
                        } else if (model.isColumnType()) {
                            listener.onTableClick(model.column);
                        }
                    }
                }
            }
        });
    }
}

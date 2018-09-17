package com.jantursky.debugger.dbviewer.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jantursky.debugger.R;
import com.jantursky.debugger.dbviewer.annotations.DbViewerSortType;
import com.jantursky.debugger.dbviewer.listeners.DbViewerGridItemListener;
import com.jantursky.debugger.dbviewer.models.DbViewerDataModel;

import java.util.ArrayList;

public class DbViewerGridRowAdapter extends RecyclerView.Adapter<DbViewerGridRowAdapter.ItemHolder> {

    private static final int TYPE_HEADER = 1;
    private static final int TYPE_ROW = 2;

    private final int colorHidden, colorNormal, colorHeader;
    private boolean isHeader;
    private boolean isSelected;
    @DbViewerSortType
    private int sortType = DbViewerSortType.A_Z;
    private int selectedHeaderSort = -1;
    private int rowPos = -1;

    private ArrayList<DbViewerDataModel> array;

    private DbViewerGridItemListener listener;

    public DbViewerGridRowAdapter(Context context, boolean isHeader) {
        this.isHeader = isHeader;

        this.colorHidden = ContextCompat.getColor(context, R.color.db_viewer_grid_bg);
        this.colorNormal = ContextCompat.getColor(context, R.color.db_viewer_grid_row_txt);
        this.colorHeader = ContextCompat.getColor(context, R.color.db_viewer_grid_row_header_txt);
//        Log.w(getClass().getSimpleName(), "##### " + array.size());
    }

    public void setOnItemClickListener(DbViewerGridItemListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeader) {
            return TYPE_HEADER;
        } else {
            return TYPE_ROW;
        }
    }

    public DbViewerDataModel getItem(int pos) {
        return array.get(pos);
    }

    public void setData(ArrayList<DbViewerDataModel> arrayList, int columns, boolean isHeader, int rowPos) {
        if (array == null) {
            array = new ArrayList<>();
        } else {
            array.clear();
        }

        this.isHeader = isHeader;
        this.rowPos = rowPos;

        if (arrayList != null && !arrayList.isEmpty()) {
            array.addAll(arrayList);
        } else {
            for (int i = 0; i < columns; i++) {
                array.add(new DbViewerDataModel(true));
            }
        }
        notifyDataSetChanged();
    }

    public void highlightRow(int row) {
        if (row == this.rowPos) {
            isSelected = true;
        } else {
            isSelected = false;
        }
        notifyDataSetChanged();
    }

    public void highlightHeader(DbViewerDataModel model, @DbViewerSortType int sortType) {
        if (isHeader) {
            for (int i = 0; i < array.size(); i++) {
                DbViewerDataModel dbViewerDataModel = array.get(i);
                if (dbViewerDataModel.dbKey.equals(model.dbKey)) {
                    this.selectedHeaderSort = i;
                    this.sortType = sortType;
                    break;
                }
            }
            notifyDataSetChanged();
        }
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        protected final FrameLayout rootLayout;
        protected final TextView txtName;
        protected TextView txtType, txtSort;
        protected View delimiter;

        public ItemHolder(View view, int viewType) {
            super(view);

            rootLayout = view.findViewById(R.id.root_layout);
            txtName = view.findViewById(R.id.name_textview);
            delimiter = view.findViewById(R.id.delimiter);
            if (viewType == TYPE_HEADER) {
                txtType = view.findViewById(R.id.header_textview);
                txtSort = view.findViewById(R.id.sort_textview);
            } else {

            }
        }
    }

    @Override
    public int getItemCount() {
        return (null != array ? (array.size()) : 0);
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        if (viewType == TYPE_HEADER) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_db_viewer_grid_header_row,
                    viewGroup, false);
        } else {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_db_viewer_grid_row,
                    viewGroup, false);
        }
        return new ItemHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemHolder holder, int position) {
        DbViewerDataModel model = getItem(position);

        if (model.isHidden) {
            holder.rootLayout.setBackgroundColor(colorHidden);
            holder.delimiter.setVisibility(View.GONE);
            holder.rootLayout.setOnClickListener(null);
        } else {
            if (position < getItemCount() - 1) {
                holder.delimiter.setVisibility(View.VISIBLE);
            } else {
                holder.delimiter.setVisibility(View.GONE);
            }

            if (model.isHeader) {
                if (position == selectedHeaderSort) {
                    if (sortType == DbViewerSortType.DISABLED) {
                        holder.txtSort.setVisibility(View.GONE);
                    } else {
                        holder.txtSort.setVisibility(View.VISIBLE);
                        holder.txtSort.setText(sortType == DbViewerSortType.A_Z ? "▲" : "▼");
                    }
                } else {
                    holder.txtSort.setVisibility(View.GONE);
                }
                holder.txtName.setTextColor(colorHeader);
                holder.rootLayout.setBackgroundResource(R.drawable.db_viewer_grid_row_header_item_selector);

                holder.rootLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int pos = holder.getAdapterPosition();
                        if (pos != -1) {
                            if (listener != null) {
                                listener.onHeaderClick(getItem(pos));
                            }
                        }
                    }
                });
            } else {
                holder.txtName.setTextColor(colorNormal);

                holder.rootLayout.setBackgroundResource(R.drawable.db_viewer_grid_row_item_selector);
                holder.rootLayout.setSelected(isSelected);

                holder.rootLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int pos = holder.getAdapterPosition();
                        if (pos != -1) {
                            if (listener != null) {
                                listener.highlightCell(rowPos);
                            }
                        }
                    }
                });

                holder.rootLayout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        int pos = holder.getAdapterPosition();
                        if (pos != -1) {
                            if (listener != null) {
                                listener.showDetailCell(getItem(pos));
                            }
                        }
                        return true;
                    }
                });
            }
        }


        String text = model.getText();
        holder.txtName.setText(text);

        if (getItemViewType(position) == TYPE_HEADER) {
            holder.txtType.setText(model.geType());
        }
    }
}

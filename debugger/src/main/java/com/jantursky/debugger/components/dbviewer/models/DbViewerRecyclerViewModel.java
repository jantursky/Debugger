package com.jantursky.debugger.components.dbviewer.models;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.jantursky.debugger.components.dbviewer.adapters.DbViewerGridRowAdapter;

public class DbViewerRecyclerViewModel {

    public View view;
    public RecyclerView recyclerView;
    public DbViewerGridRowAdapter adapter;

    public DbViewerRecyclerViewModel() {}

    public DbViewerRecyclerViewModel(View view, RecyclerView recyclerView, DbViewerGridRowAdapter adapter) {
        this.view = view;
        this.recyclerView = recyclerView;
        this.adapter = adapter;
    }

    public boolean areValidData() {
        return view != null && recyclerView != null && adapter != null;
    }
}

package com.jantursky.debugger.dbviewer.models;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jantursky.debugger.dbviewer.adapters.DbViewerGridRowAdapter;

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

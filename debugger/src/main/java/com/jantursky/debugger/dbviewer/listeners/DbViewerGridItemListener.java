package com.jantursky.debugger.dbviewer.listeners;

import com.jantursky.debugger.dbviewer.models.DbViewerDataModel;

public interface DbViewerGridItemListener {

    void onHeaderClick(DbViewerDataModel model);

    void highlightCell(int row);

    void showDetailCell(DbViewerDataModel model);

}

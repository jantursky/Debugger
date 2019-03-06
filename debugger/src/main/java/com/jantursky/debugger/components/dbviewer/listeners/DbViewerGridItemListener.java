package com.jantursky.debugger.components.dbviewer.listeners;

import com.jantursky.debugger.components.dbviewer.models.DbViewerDataModel;

public interface DbViewerGridItemListener {

    void onHeaderClick(DbViewerDataModel model);

    void highlightCell(int row);

    void deleteRow(DbViewerDataModel model);

    void showDetailCell(DbViewerDataModel model);

}

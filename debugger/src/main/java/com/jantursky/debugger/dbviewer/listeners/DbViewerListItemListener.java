package com.jantursky.debugger.dbviewer.listeners;

import java.io.File;

public interface DbViewerListItemListener {

    void goBack();

    void onDatabaseClick(File databaseFile);

    void onTableClick(String table);

}

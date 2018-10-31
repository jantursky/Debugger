package com.jantursky.debugger.components.dbviewer;

import android.content.Context;
import android.view.View;

import com.jantursky.debugger.components.DebuggerComponent;
import com.jantursky.debugger.annotations.DebuggerComponentType;
import com.jantursky.debugger.interfaces.ComponentListener;

public class DbViewerComponent extends DebuggerComponent {

    private static final String TAG = DbViewerComponent.class.getSimpleName();

    public DbViewerComponent() {
        super(DebuggerComponentType.DB_VIEWER);
    }

    @Override
    public View createView(Context context, ComponentListener listener) {
        DbGridView view = new DbGridView(context);
        view.setComponentListener(listener);
        return view;
    }
}
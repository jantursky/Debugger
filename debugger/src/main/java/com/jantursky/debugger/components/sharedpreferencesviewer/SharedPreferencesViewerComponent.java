package com.jantursky.debugger.components.sharedpreferencesviewer;

import android.content.Context;
import android.view.View;

import com.jantursky.debugger.annotations.DebuggerComponentType;
import com.jantursky.debugger.components.DebuggerComponent;
import com.jantursky.debugger.interfaces.ComponentListener;

public class SharedPreferencesViewerComponent extends DebuggerComponent {

    private static final String TAG = SharedPreferencesViewerComponent.class.getSimpleName();

    public SharedPreferencesViewerComponent() {
        super(DebuggerComponentType.SHARED_PREFERENCES_VIEWER);
    }

    @Override
    public View createView(Context context, ComponentListener listener) {
        SharedPreferencesView view = new SharedPreferencesView(context);
        view.setComponentListener(listener);
        return view;
    }
}
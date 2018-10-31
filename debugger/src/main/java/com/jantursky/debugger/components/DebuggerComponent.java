package com.jantursky.debugger.components;

import android.content.Context;
import android.view.View;

import com.jantursky.debugger.annotations.DebuggerComponentType;
import com.jantursky.debugger.interfaces.ComponentListener;

public abstract class DebuggerComponent {

    @DebuggerComponentType
    public final int componentType;

    public DebuggerComponent(@DebuggerComponentType int componentType) {
        this.componentType = componentType;
    }

    public abstract View createView(Context context, ComponentListener listener);

}

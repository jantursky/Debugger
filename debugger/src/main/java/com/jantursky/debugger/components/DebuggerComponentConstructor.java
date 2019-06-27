package com.jantursky.debugger.components;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.jantursky.debugger.adapters.DebuggerComponentAdapter;
import com.jantursky.debugger.listeners.DebuggerComponentItemListener;
import com.jantursky.debugger.interfaces.ComponentListener;
import com.jantursky.debugger.utils.RecyclerViewUtils;

import java.util.ArrayList;

public class DebuggerComponentConstructor implements DebuggerComponentItemListener, ComponentListener {

    private static final String TAG = DebuggerComponentConstructor.class.getSimpleName();
    private final RecyclerView recyclerViewList;
    private final DebuggerComponentAdapter adapter;
    private final ViewGroup viewGroup;
    private final Context context;

    public DebuggerComponentConstructor(Context context, ViewGroup viewGroupToAdd, ArrayList<DebuggerComponent> types) {
        this.viewGroup = viewGroupToAdd;
        this.context = context;

        this.viewGroup.setBackgroundColor(Color.WHITE);

        recyclerViewList = new RecyclerView(context);
        recyclerViewList.setHasFixedSize(true);
        recyclerViewList.addItemDecoration(RecyclerViewUtils.getVerticalItemDecoration(context));
        recyclerViewList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerViewList.setAdapter(adapter = new DebuggerComponentAdapter(types));
        adapter.setOnItemClickListener(this);

        showAllComponents();
    }

    private void showAllComponents() {
        this.viewGroup.removeAllViews();
        this.viewGroup.addView(recyclerViewList);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void onItemSelect(DebuggerComponent debuggerComponent) {
        viewGroup.removeAllViews();
        viewGroup.addView(debuggerComponent.createView(context, this));
    }

    @Override
    public void closeComponent() {
        showAllComponents();
    }

    public static class Builder {

        protected ArrayList<DebuggerComponent> types;

        public Builder() {
        }

        public DebuggerComponentConstructor build(Activity context, ViewGroup viewGroupToAdd) {
            return new DebuggerComponentConstructor(context, viewGroupToAdd, types);
        }

        public Builder add(DebuggerComponent component) {
            if (types == null) {
                types = new ArrayList<>();
            }
            types.add(component);
            return this;
        }

        public DebuggerComponentConstructor build(Activity activity, int viewToAdd) {
            return new DebuggerComponentConstructor(
                    activity,
                    (ViewGroup) activity.findViewById(viewToAdd),
                    types);
        }

        public DebuggerComponentConstructor build(Fragment fragment, int viewToAdd) {
            return new DebuggerComponentConstructor(
                    fragment.getActivity(),
                    (ViewGroup) fragment.getView().findViewById(viewToAdd),
                    types);
        }

        public DebuggerComponentConstructor build(Context context, View view, int viewToAdd) {
            return new DebuggerComponentConstructor(
                    context,
                    (ViewGroup) view.findViewById(viewToAdd),
                    types);
        }

        public DebuggerComponentConstructor build(Context context, ViewGroup view) {
            return new DebuggerComponentConstructor(
                    context,
                    view,
                    types);
        }
    }

}
package com.jantursky.debugger.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;

import com.jantursky.debugger.R;

public class RecyclerViewUtils {

    public static DividerItemDecoration getVerticalItemDecoration(Context context) {
        DividerItemDecoration decoration = new DividerItemDecoration(context, RecyclerView.VERTICAL);
        decoration.setDrawable(ContextCompat.getDrawable(context, R.drawable.list_divider));
        return decoration;
    }

}

// eof

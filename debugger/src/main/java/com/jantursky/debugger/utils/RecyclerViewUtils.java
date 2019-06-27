package com.jantursky.debugger.utils;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.jantursky.debugger.R;

public class RecyclerViewUtils {

    public static DividerItemDecoration getVerticalItemDecoration(Context context) {
        DividerItemDecoration decoration = new DividerItemDecoration(context, RecyclerView.VERTICAL);
        decoration.setDrawable(ContextCompat.getDrawable(context, R.drawable.list_divider));
        return decoration;
    }

}

// eof

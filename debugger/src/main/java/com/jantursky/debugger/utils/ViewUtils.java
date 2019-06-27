package com.jantursky.debugger.utils;

import android.os.Build;
import androidx.core.view.ViewCompat;
import android.view.View;
import android.view.ViewTreeObserver;

import com.jantursky.debugger.interfaces.GlobalViewListener;

public class ViewUtils {

    private static final String TAG = ViewUtils.class.getSimpleName();

    public static void addListener(final View view, final GlobalViewListener listener) {
        if (view != null && ViewCompat.isAttachedToWindow(view)) {
            if (listener != null) {
                listener.onGlobalLayout(view.getMeasuredWidth(), view.getMeasuredHeight());
            }
        } else {
            view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (view != null && listener != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        } else {
                            view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                        listener.onGlobalLayout(view.getWidth(), view.getHeight());
                    }
                }
            });
        }
    }
}
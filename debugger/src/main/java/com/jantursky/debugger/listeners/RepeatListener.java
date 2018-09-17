package com.jantursky.debugger.listeners;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

/**
 * A class, that can be used as a TouchListener on any view (e.g. a Button).
 * It cyclically runs a clickListener, emulating keyboard-like behaviour. First
 * click is fired immediately, next one after the initialInterval, and subsequent
 * ones after the normalInterval.
 * <p>
 * <p>Interval is scheduled after the onClick completes, so it has to run fast.
 * If it runs slow, it does not generate skipped onClicks. Can be rewritten to
 * achieve this.
 */
public class RepeatListener implements OnTouchListener {

    private static final String TAG = RepeatListener.class.getSimpleName();
    private final OnClickListener clickListener;
    private Handler handler = new Handler();
    private long initialRepeatDelay;
    private long repeatIntervalInMilliseconds;
    // speedup
    private long repeatIntervalCurrent = repeatIntervalInMilliseconds;
    private long repeatIntervalStep = 10;
    private long repeatIntervalMin = 10;
    private boolean incrementSpeed;
    private View downView;
    private Runnable handlerRunnable = new Runnable() {
        @Override
        public void run() {
            clickListener.onClick(downView);

            if (incrementSpeed) {
                if (repeatIntervalCurrent > repeatIntervalMin) {
                    repeatIntervalCurrent -= repeatIntervalStep;
//                Log.w(TAG, "###### " + repeatIntervalCurrent);
                }
            }

            handler.postDelayed(this, repeatIntervalCurrent);
        }
    };


    /**
     * @param initialRepeatDelay           The interval after first click event
     * @param repeatIntervalInMilliseconds The interval after second and subsequent click
     *                                     events
     * @param clickListener                The OnClickListener, that will be called
     *                                     periodically
     */
    public RepeatListener(long initialRepeatDelay, long repeatIntervalInMilliseconds, boolean incrementSpeed,
                          OnClickListener clickListener) {
        if (clickListener == null)
            throw new IllegalArgumentException("null runnable");
        if (initialRepeatDelay < 0 || repeatIntervalInMilliseconds < 0)
            throw new IllegalArgumentException("negative interval");

        this.initialRepeatDelay = initialRepeatDelay;
        this.repeatIntervalInMilliseconds = repeatIntervalInMilliseconds;
        this.clickListener = clickListener;
        this.incrementSpeed = incrementSpeed;
    }

    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            handler.removeCallbacks(handlerRunnable);
            repeatIntervalCurrent = repeatIntervalInMilliseconds;
            handler.postDelayed(handlerRunnable, initialRepeatDelay);
            downView = view;
            downView.setPressed(true);
            clickListener.onClick(view);
            return true;
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            handler.removeCallbacks(handlerRunnable);
            downView.setPressed(false);
            downView = null;
            return true;
        }

        return false;
    }

}
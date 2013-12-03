package com.jpardogo.listbuddies.lib.views.containers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.jpardogo.listbuddies.lib.R;

/**
 * LinearLayout with a touch feedback color as overlay.
 */
public class LinearLayoutFeedback extends LinearLayout {

    private Drawable touchFeedbackDrawable;

    public LinearLayoutFeedback(Context context) {
        super(context);
    }

    public LinearLayoutFeedback(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LinearLayoutFeedback(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //TODO make the color of the drawable variable passong it as a xml parameter
        touchFeedbackDrawable = getResources().getDrawable(R.drawable.touch_selector);
    }

    @Override
    public void setPressed(boolean pressed) {
        // If the parent is pressed, do not set to pressed.
        if (pressed && ((View) getParent()).isPressed()) {
            return;
        }
        super.setPressed(pressed);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        touchFeedbackDrawable.setBounds(0, 0, getWidth(), getHeight());
        touchFeedbackDrawable.draw(canvas);
    }

    @Override
    protected void drawableStateChanged() {
        if (touchFeedbackDrawable != null) {
            touchFeedbackDrawable.setState(getDrawableState());
            invalidate();
        }
        super.drawableStateChanged();
    }
}

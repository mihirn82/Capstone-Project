package com.gmail.mihirn82.myteleprompter;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

public class MirroredTextView extends android.support.v7.widget.AppCompatTextView {

    public static boolean mirror;

    public MirroredTextView(Context context, AttributeSet attrs) {
        super(context,attrs);
    }

    protected void onDraw(Canvas canvas) {
        if (mirror) {
            canvas.translate((float) getWidth(), 0.0f);
            canvas.scale(-1.0f, 1.0f);
        }
        super.onDraw(canvas);
    }
}

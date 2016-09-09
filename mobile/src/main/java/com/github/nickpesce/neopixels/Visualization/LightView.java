package com.github.nickpesce.neopixels.Visualization;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.github.nickpesce.neopixels.MainActivity;
import com.github.nickpesce.neopixels.R;

/**
 * TODO: document your custom view class.
 */
public class LightView extends View {
    private Drawable mExampleDrawable;
    private byte[] pixels;

    public void updatePixels(byte[] pixels) {
        this.pixels = pixels;
        postInvalidate();
    }

    public LightView(Context context) {
        super(context);
    }

    public LightView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LightView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(pixels == null) return;
        int lightWidth = (int)(((float)getWidth())/MainActivity.NUM_LIGHTS);
        Paint paint = new Paint();
        for(int i = 0; i < MainActivity.NUM_LIGHTS; i++) {
            System.out.println(pixels[3*i] + " " + pixels[3*i+1] + " " + pixels[3*i+2]);
            paint.setARGB(255, pixels[3*i], pixels[3*i+1], pixels[3*i+2]);
            canvas.drawRect(i*lightWidth, 0, (i+1)*lightWidth, lightWidth, paint);

        }
    }
}

package com.khoa.tudien.Model;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


public class ChooseView extends View {

    private Paint rectanglePaint;
    private int widthRect, heightRect, centerY, centerX, widthParent, heightParent;
    private int n = 4;

    public ChooseView(Context context) {
        super(context);
        initPaint();
    }

    public ChooseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public ChooseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();

    }

    public ChooseView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initPaint();
    }

    public void setNumberChar(int n){
        this.n = n;
    }

    public void initPaint() {
        rectanglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rectanglePaint.setStyle(Paint.Style.FILL);
        rectanglePaint.setColor(Color.BLUE);
        widthRect = 70;
        heightRect = widthRect * 4 / 3;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        widthParent = MeasureSpec.getSize(widthMeasureSpec);
        heightParent = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthParent, heightParent);
        centerY = heightParent / 2;
        centerX = widthParent / 2;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        int[] x = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            if (i == 1) {
                if ((n / 2) * 2 == n) {
                    int p = n / 2;
                    x[i] = centerX - widthRect * (10 * p - 1) / 8;
                } else {
                    int p = (n - 1) / 2;
                    x[i] = centerX - (widthRect * (2 + 5 * p) / 4);
                }
            } else {
                x[i] = x[i - 1] + 5 * widthRect / 4;
            }
            canvas.drawRect(x[i], centerY - heightRect / 2
                    , x[i] + widthRect
                    , centerY - heightRect / 2 + heightRect, rectanglePaint);
        }

    }
}

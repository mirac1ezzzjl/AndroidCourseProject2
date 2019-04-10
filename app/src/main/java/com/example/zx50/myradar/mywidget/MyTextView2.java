package com.example.zx50.myradar.mywidget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyTextView2 extends android.support.v7.widget.AppCompatTextView {
    private TextView borderText = null;///用于描边的TextView

    public MyTextView2(Context context) {
        super(context);
        borderText = new TextView(context);
        init();
    }

    public MyTextView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        borderText = new TextView(context, attrs);
        init();
    }

    public MyTextView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        borderText = new TextView(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "04B_03B_.TTF");
        borderText.setTypeface(typeface);
        TextPaint tp1 = borderText.getPaint();
        tp1.setStrokeWidth(2); //设置描边宽度
        tp1.setStyle(Paint.Style.STROKE); //对文字只描边
        borderText.setTextColor(Color.BLACK); //设置描边颜色
        borderText.setGravity(getGravity());

    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);
        borderText.setLayoutParams(params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        CharSequence tt = borderText.getText();

        //两个TextView上的文字必须一致
        if (tt == null || !tt.equals(this.getText())) {
            borderText.setText(getText());
            this.postInvalidate();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        borderText.measure(widthMeasureSpec, heightMeasureSpec);
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        borderText.layout(left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        borderText.draw(canvas);
        super.onDraw(canvas);
    }

}

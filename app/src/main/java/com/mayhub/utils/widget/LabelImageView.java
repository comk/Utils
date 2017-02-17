package com.mayhub.utils.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;


/**
 * Created by Administrator on 2016/9/12.
 */
public class LabelImageView extends ImageView {

    private int marginTop;

    private int marginLeft;

    private int txtColor = Color.BLACK;

    private int labelBgColor = Color.WHITE;

    private int txtPadding;

    private String txt;

    private int txtSize = 20;

    private Paint paint = new Paint();

    private RectF rect;

    public LabelImageView(Context context) {
        this(context, null);
    }

    public LabelImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LabelImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        rect = new RectF();

    }

    public void setTxtColor(int txtColor) {
        this.txtColor = txtColor;
        invalidate();
    }

    public void setLabelBgColor(int labelBgColor) {
        this.labelBgColor = labelBgColor;
        invalidate();
    }

    public void setMarginTop(int marginTop) {
        this.marginTop = marginTop;
        invalidate();
    }

    public void setMarginLeft(int marginLeft) {
        this.marginLeft = marginLeft;
        invalidate();
    }

    public void setTxtPadding(int txtPadding) {
        this.txtPadding = txtPadding;
        invalidate();
    }

    public void setTxt(String txt) {
        this.txt = txt;
        invalidate();
    }

    public void setTxtSize(int txtSize) {
        this.txtSize = txtSize;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(!TextUtils.isEmpty(txt)) {
            paint.setTextSize(txtSize);
            float txtLen = paint.measureText(txt);
            float width = txtLen + (2 * txtPadding);
            float height = txtSize + (2 * txtPadding);
            paint.setColor(labelBgColor);
            rect.set(marginLeft, marginTop, marginLeft + width - (height / 2), marginTop + height);
            canvas.drawCircle(marginLeft + width - (height / 2), marginTop + (height / 2), height / 2, paint);
            canvas.drawRect(rect, paint);
            paint.setColor(txtColor);
            Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
            int baseLineY = (int) (rect.centerY() - fontMetrics.top / 2 - fontMetrics.bottom / 2);
            canvas.drawText(txt, marginLeft + txtPadding, baseLineY, paint);
        }
    }
}

package com.asmara.app;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

public class BlurGlassView extends FrameLayout {

    private int baseColor = Color.parseColor("#CFECF3");
    private float cornerRadius = 64f;
    private float elevationDp = 4f;

    private Paint basePaint;
    private Paint lightPaint;
    private Paint shadowPaint;
    private RectF rect;
    private Path basePath;
    private Path inversePath;

    public BlurGlassView(Context context) {
        super(context);
        init(context, null);
    }

    public BlurGlassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BlurGlassView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            // Kita tetap menggunakan nama styleable lama agar XML tidak perlu diubah
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BlurGlassView);
            baseColor = a.getColor(R.styleable.BlurGlassView_glassColor, baseColor);
            cornerRadius = a.getDimension(R.styleable.BlurGlassView_glassCornerRadius, dpToPx(24));
            a.recycle();
        } else {
            cornerRadius = dpToPx(24);
        }

        // Kita gambar shadow manual karena elevation native jelek dengan software layer
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        setBackground(null); // hapus background bawaan
        setElevation(0);
        setWillNotDraw(false);

        rect = new RectF();
        basePath = new Path();
        inversePath = new Path();

        basePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        
        lightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        lightPaint.setStyle(Paint.Style.FILL);
        lightPaint.setColor(Color.parseColor("#E6FFFFFF")); // Inner light
        lightPaint.setMaskFilter(new BlurMaskFilter(dpToPx(6f), BlurMaskFilter.Blur.NORMAL));
        lightPaint.setXfermode(new android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_ATOP));

        shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shadowPaint.setStyle(Paint.Style.FILL);
        shadowPaint.setColor(Color.parseColor("#40000000")); // Inner dark shadow
        shadowPaint.setMaskFilter(new BlurMaskFilter(dpToPx(6f), BlurMaskFilter.Blur.NORMAL));
        shadowPaint.setXfermode(new android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_ATOP));

        updateColor();
    }

    public void setGlassColor(int color) {
        this.baseColor = color;
        updateColor();
    }

    private void updateColor() {
        int solidColor = Color.rgb(Color.red(baseColor), Color.green(baseColor), Color.blue(baseColor));
        basePaint.setColor(solidColor);
        invalidate();
    }

    private float dpToPx(float dp) {
        return dp * getResources().getDisplayMetrics().density;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        
        // Buat margin untuk drop shadow luar
        float margin = dpToPx(elevationDp) + dpToPx(2f);
        rect.set(margin, margin, w - margin, h - margin);
        
        basePath.reset();
        basePath.addRoundRect(rect, cornerRadius, cornerRadius, Path.Direction.CW);
        
        inversePath.reset();
        inversePath.setFillType(Path.FillType.EVEN_ODD); 
        float expand = dpToPx(40f); 
        inversePath.addRect(-expand, -expand, w + expand, h + expand, Path.Direction.CW);
        inversePath.addPath(basePath); 
        
        invalidateOutline();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (basePath == null) return;
        
        // 1. Gambar Outer Drop Shadow manual
        Paint dropShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dropShadowPaint.setColor(Color.parseColor("#25000000"));
        dropShadowPaint.setMaskFilter(new BlurMaskFilter(dpToPx(elevationDp), BlurMaskFilter.Blur.NORMAL));
        canvas.save();
        canvas.translate(0, dpToPx(3f)); // shadow agak ke bawah
        canvas.drawPath(basePath, dropShadowPaint);
        canvas.restore();

        // 2. Gunakan offscreen buffer untuk shape dan inner shadow
        int saveCount = canvas.saveLayer(0, 0, getWidth(), getHeight(), null);
        
        // Gambar base shape
        canvas.drawPath(basePath, basePaint);

        // Gambar Inner Highlight (Kiri Atas)
        canvas.save();
        canvas.translate(dpToPx(3f), dpToPx(3f));
        canvas.drawPath(inversePath, lightPaint);
        canvas.restore();

        // Gambar Inner Shadow (Kanan Bawah)
        canvas.save();
        canvas.translate(-dpToPx(3f), -dpToPx(3f));
        canvas.drawPath(inversePath, shadowPaint);
        canvas.restore();
        
        canvas.restoreToCount(saveCount);
    }
}

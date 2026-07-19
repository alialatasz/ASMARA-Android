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

        // Gunakan GradientDrawable native sebagai background parent.
        // Ini adalah trik rahasia agar OS Android menggambar bayangan drop shadow (elevation)
        // 100% sempurna membulat tanpa bug kotak, karena OS mengenali bentuk background ini.
        android.graphics.drawable.GradientDrawable bgDrawable = new android.graphics.drawable.GradientDrawable();
        bgDrawable.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
        bgDrawable.setCornerRadius(cornerRadius);
        int solidColor = Color.rgb(Color.red(baseColor), Color.green(baseColor), Color.blue(baseColor));
        bgDrawable.setColor(solidColor);
        setBackground(bgDrawable);

        setElevation(dpToPx(elevationDp));
        
        setOutlineProvider(new android.view.ViewOutlineProvider() {
            @Override
            public void getOutline(View view, android.graphics.Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), cornerRadius);
            }
        });
        setClipToOutline(true);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        setWillNotDraw(false);

        rect = new RectF();
        basePath = new Path();
        inversePath = new Path();

        basePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        
        lightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        lightPaint.setStyle(Paint.Style.FILL);
        lightPaint.setColor(Color.parseColor("#99FFFFFF")); 
        lightPaint.setMaskFilter(new BlurMaskFilter(dpToPx(8f), BlurMaskFilter.Blur.NORMAL));
        lightPaint.setXfermode(new android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_ATOP));

        shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shadowPaint.setStyle(Paint.Style.FILL);
        shadowPaint.setColor(Color.parseColor("#11000000")); 
        shadowPaint.setMaskFilter(new BlurMaskFilter(dpToPx(8f), BlurMaskFilter.Blur.NORMAL));
        shadowPaint.setXfermode(new android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_ATOP));

        updateColor();
    }

    public void setGlassColor(int color) {
        this.baseColor = color;
        updateColor();
    }
    
    private void updateColor() {
        if (basePaint == null) return;
        int solidColor = Color.rgb(Color.red(baseColor), Color.green(baseColor), Color.blue(baseColor));
        if (getBackground() instanceof android.graphics.drawable.GradientDrawable) {
            ((android.graphics.drawable.GradientDrawable) getBackground()).setColor(solidColor);
        }
        basePaint.setColor(solidColor);
        invalidate();
    }

    public void setBlurRadius(float radius) {
    }

    private float dpToPx(float dp) {
        return dp * getResources().getDisplayMetrics().density;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        rect.set(0, 0, w, h);
        
        basePath.reset();
        basePath.addRoundRect(rect, cornerRadius, cornerRadius, Path.Direction.CW);
        
        inversePath.reset();
        inversePath.setFillType(Path.FillType.EVEN_ODD); 
        float expand = dpToPx(60f); 
        inversePath.addRect(-expand, -expand, w + expand, h + expand, Path.Direction.CW);
        inversePath.addPath(basePath); 
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (basePath == null) return;
        
        // Gunakan offscreen buffer (saveLayer) agar efek SRC_ATOP hanya berlaku 
        // di dalam area basePath dan tidak tumpah/meluber ke luar batas kotak!
        int saveCount = canvas.saveLayer(0, 0, getWidth(), getHeight(), null);
        
        canvas.drawPath(basePath, basePaint);

        canvas.save();
        canvas.translate(dpToPx(4f), dpToPx(4f));
        canvas.drawPath(inversePath, lightPaint);
        canvas.restore();

        canvas.save();
        canvas.translate(-dpToPx(4f), -dpToPx(4f));
        canvas.drawPath(inversePath, shadowPaint);
        canvas.restore();
        
        canvas.restoreToCount(saveCount);
    }
}

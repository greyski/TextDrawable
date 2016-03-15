package com.fleksy.textdrawable;

import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;

/**
 * Created by Fleksy.
 * Author Greyski.
 */
abstract class BaseDrawable extends Drawable {

    protected final TextPaint paint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
    private final Rect currBounds = new Rect();
    private int id = 0;
    private int currentColor = 0;
    private boolean display = true;

    public BaseDrawable() {
        this(0, true);
    }

    public BaseDrawable(int color, boolean filled) {
        setFilled(filled);
        setColor(color);
        paint.setAntiAlias(true);
        paint.setSubpixelText(true);
    }

    public final int getId() {
        return id;
    }

    public final void setId(int id) {
        this.id = id;
    }

    protected abstract void createPath(Rect bounds);

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        createPath(bounds);
        currBounds.set(bounds);
    }

    public Rect getCurrentBounds() {
        return currBounds;
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        paint.setColorFilter(cf);
    }

    public void setBounds(float left, float top, float right, float bottom) {
        setBounds(Math.round(left), Math.round(top), Math.round(right), Math.round(bottom));
    }

    public void setBounds(RectF bounds) {
        setBounds(bounds.left, bounds.top, bounds.right, bounds.bottom);
    }

    @Override
    public int getOpacity() {
        return 0;
    }

    public void setFilled(boolean filled) {
        paint.setStyle(filled ? Style.FILL : Style.STROKE);
    }

    public boolean shown() {
        return display;
    }

    public void display(boolean display) {
        this.display = display;
    }

    public Paint getPaint() {
        return paint;
    }

    public int getColor() {
        return paint.getColor();
    }

    public final void setColor(int color) {
        onCancelAnimations();
        animateColor(color);
    }

    public void setOutline(int color) {
        onCancelAnimations();
        paint.setShadowLayer(10, 0, 0, color);
    }

    protected void animateColor(int color) {
        currentColor = color;
        paint.setColor(color);
    }

    public int getAlpha() {
        return paint.getAlpha();
    }

    @Override
    public void setAlpha(int alpha) {
        onCancelAnimations();
        paint.setAlpha(alpha);
    }

    public final boolean contains(float x, float y) {
        return new RectF(copyBounds()).contains(x, y);
    }

    public final float getAlphaRatio() {
        return Math.min(1.0f, (getAlpha() * 1.0f) / (CharacterUtils.MAX_ARGB * 1.0f));
    }

    public final void setAlphaRatio(float ratio, boolean fullAlpha) {
        onCancelAnimations();
        animateAlphaRatio(ratio, fullAlpha);
    }

    protected int animateAlphaRatio(float ratio, boolean fullAlpha) {
        final int alpha = fullAlpha ? CharacterUtils.MAX_ARGB : Color.alpha(currentColor);
        paint.setAlpha((int) (alpha * ratio));
        return paint.getAlpha();
    }

    @Override
    public ConstantState getConstantState() {
        return new ConstantState() {
            @Override
            public Drawable newDrawable() {
                return BaseDrawable.this;
            }

            @Override
            public int getChangingConfigurations() {
                return 0;
            }
        };
    }

    protected void onCancelAnimations() {
        //Override where necessary
    }

}

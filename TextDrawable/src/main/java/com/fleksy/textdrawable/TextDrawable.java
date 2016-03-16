package com.fleksy.textdrawable;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;

/**
 * Created by Fleksy.
 * Author Greyski.
 */
public class TextDrawable extends BaseDrawable {

    private CharSequence text = "";

    private boolean isRTL = false;
    private boolean includeFontSpacing = false;

    private float sizeText = 0;
    private float scaleText = 1;
    private float moveX = 0, moveY = 0;
    private float centerX = 0, centerY = 0;

    private StaticLayout textLayout;
    private Alignment align = Alignment.ALIGN_CENTER;

    public TextDrawable() {
        this("");
    }

    public TextDrawable(String text) {
        this(text, null);
    }

    public TextDrawable(String text, Typeface typeface) {
        this(text, typeface, 0, 0);
    }

    public TextDrawable(String text, Typeface typeface, float size, int color) {
        this(text, typeface, size, color, 0);
    }

    public TextDrawable(String text, Typeface typeface, float size, int color, int trimColor) {
        super(color, true);
        this.text = text == null ? "" : text;
        setTypeFace(typeface);
        if (trimColor != 0) {
            setOutline(trimColor);
        }
        setTextSize(size);
        defaultBounds();
    }

    private void createLayout(Rect bounds) {
        textLayout = new StaticLayout(text, paint,
                Math.max(bounds.width(), 0),
                align, 1.0f, 0.0f, false);
    }

    @Override
    protected void createPath(Rect bounds) {
        centerX = bounds.exactCenterX();
        centerY = bounds.exactCenterY();
        switch (paint.getTextAlign()) {
            case RIGHT:
                translateX(bounds.width() / 2);
                break;
            case LEFT:
                translateX(-bounds.width() / 2);
                break;
            default:
                translateX(0);
                break;
        }
        createLayout(bounds);
    }

    @Override
    public void draw(Canvas canvas) {
        if (shown() && textLayout != null) {
            canvas.save();
            canvas.translate(centerX + moveX, getBaseline());
            textLayout.draw(canvas);
            canvas.restore();
        }
    }

    public void setRTL(boolean rtl) {
        this.isRTL = rtl;
    }

    public boolean isRTL() {
        return isRTL;
    }

    public String getText() {
        return text.toString();
    }

    public boolean setText(CharSequence text) {
        return setText((String) text);
    }

    public boolean setText(String text) {
        if (text == null) {
            text = "";
        }
        final boolean changedText = !this.text.equals(text);
        this.text = text;
        if (changedText || textLayout == null) {
            createLayout(getCurrentBounds());
        }
        return changedText;
    }

    public void setTextSize(float size) {
        sizeText = size;
        paint.setTextSize(size);
    }

    public void setTypeFace(Typeface font) {
        paint.setTypeface(font);
    }

    public float getTextSize() {
        return sizeText;
    }

    public Typeface getTypeFace() {
        return paint.getTypeface();
    }

    public void translateX(float x) {
        moveX = x;
    }

    public float getBaseline() {
        return centerY + getYPositioning(paint, scaleText, sizeText) + moveY;
    }

    public float getRawBaseline() {
        return textLayout.getLineBaseline(0) + getBaseline();
    }

    public void translateY(float y) {
        moveY = y;
    }

    public void setTextAlign(Alignment align) {
        this.align = align;
    }

    public Rect getDefaultBounds() {
        return defaultBounds(paint, text.toString(),
                scaleText, sizeText, includeFontSpacing);
    }

    public Rect defaultBounds() {
        final Rect bounds = getDefaultBounds();
        setBounds(bounds);
        return bounds;
    }

    public void setIncludeFontSpacing(boolean includeFontSpacing) {
        this.includeFontSpacing = includeFontSpacing;
    }

    public boolean isMultiline() {
        return text.toString().split("\r\n|\r|\n").length > 1;
    }

    public int getTextWidth() {
        final Rect bounds = new Rect();
        paint.getTextBounds(text.toString(), 0,
                text.toString().length(), bounds);
        return bounds.width();
    }

    public boolean isBold() {
        return paint.isFakeBoldText();
    }

    public void setBold(boolean bold) {
        paint.setFakeBoldText(bold);
    }

    public float getTextScale() {
        return scaleText;
    }

    public void setTextScale(float scale) {
        scaleText = scale;
    }

    public void reset() {
        display(true);
        setColor(0);
    }

    private static int getYPositioning(TextPaint paint, float textScale, float textSize) {
        paint.setTextSize(textSize * textScale);
        final int in = Math.round(((paint.ascent() - paint.descent()) / (textScale * 2.0f)));
        return (int) (in / textScale);
    }

    private static Rect defaultBounds(TextPaint paint, String text, float scaleText, float sizeText, boolean includeFontSpacing) {
        if (text == null) {
            text = "";
        }
        int maxWidth = (int) paint.measureText(text);
        int maxHeight = 0;
        final String[] lines = text.split("\r\n|\r|\n");
        for (String line : lines) {
            final Rect bounds = new Rect();
            paint.getTextBounds(line, 0, line.length(), bounds);
            maxWidth = Math.max(bounds.width(), maxWidth);
            maxHeight += bounds.height();
        }
        if (includeFontSpacing) {
            maxWidth += paint.getFontSpacing();
        }
        return new Rect(0, 0, maxWidth, maxHeight +
                Math.abs(getYPositioning(paint, scaleText, sizeText)));
    }

    public static Rect defaultBounds(String text, float sizeText) {
        final TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(sizeText);
        return defaultBounds(paint, text, 1, sizeText, true);
    }


    public static float autoScaleText(final String text, final float parentW, final float parentH, float targetTextSize) {

        final float wantedSize = targetTextSize;

        final Rect bounds = new Rect();
        final TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(targetTextSize);
        paint.getTextBounds(text, 0, text.length(), bounds);

        if (bounds.width() > 0 && bounds.height() > 0) {

            while (!fitsParent(text, paint, parentW, parentH, targetTextSize) && targetTextSize > 0) {
                targetTextSize = Math.max(targetTextSize - 1, 0);
            }
            if (targetTextSize <= 0) {
                targetTextSize = wantedSize;
                paint.setTextSize(wantedSize);
            }
            return targetTextSize;
        }

        return targetTextSize;
    }

    private static boolean fitsParent(final String text, final TextPaint paint, final float parentW, final float parentH, float textSize) {
        paint.setTextSize(textSize);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        StaticLayout textLayout = new StaticLayout(text, paint, bounds.width(), Alignment.ALIGN_NORMAL, 0.0f, 0.0f, false);
        return (textLayout.getHeight() <= parentH && textLayout.getWidth() <= parentW) &&
                (Math.abs(parentH - textLayout.getHeight()) >= 0 &&
                        Math.abs(parentW - textLayout.getWidth()) >= 0);
    }

}

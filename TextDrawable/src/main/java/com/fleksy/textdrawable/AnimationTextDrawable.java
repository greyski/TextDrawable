package com.fleksy.textdrawable;

import android.animation.Animator;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Edited by Fleksy on 2/24/16.
 * Editor Greyski.
 * Author hanks on 15-12-14.
 */
abstract class AnimationTextDrawable extends TextDrawable {

    /**
     * Old paint to keep track of previous paint state of text
     */
    private TextPaint oldPaint;

    /**
     * the gap between characters...come on, it can't be more than 100
     */
    protected float[] gaps = new float[100];
    protected float[] oldGaps = new float[100];

    /**
     * Ye olde text that will be animated into new text
     */
    protected CharSequence oldText;

    /**
     * Contains list of character elements that are different between old and new text
     */
    protected List<CharacterUtils.CharacterDiffResult> differentList = new ArrayList<>();

    protected float oldStartX = 0; // Old start X of string value
    protected float startX = 0; // Latest start X of string
    protected float startY = 0; // Latest start Y found from baseline

    public AnimationTextDrawable() {
        super();
    }

    /**
     * Transition current text into new text passed in.
     *
     * @param text the text we want to animate into
     * @param rtlLanguage Necessary for inverting the String for the animation (may be Fleksy specific)
     * @param parent View that holds the drawable
     * @return Animator object that will perform the animation
     */
    public Animator animateText(CharSequence text, boolean rtlLanguage, View parent) {
        setRTL(rtlLanguage);
        oldText = getText();
        text = CharacterUtils.getAlignedText(text.toString(), rtlLanguage);
        setText(text);
        prepareAnimate();
        animatePrepare(text);
        return animate(text, parent);
    }

    /**
     * Resets the text and cancels the animation
     *
     * @param text to reset to
     * @param parent View parent to invalidate
     */
    public void reset(CharSequence text, View parent) {
        animatePrepare(text);
        parent.invalidate();
    }

    /**
     * Provides the old Paint of the animated text
     *
     * @return the old TextPaint
     */
    protected final TextPaint getOldPaint() {
        if (oldPaint == null) {
            oldPaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
        }
        return oldPaint;
    }

    /**
     * Both prepares and essentially clears the animation data and sets up the text to be animated
     * between old and new.
     */
    private void prepareAnimate() {

        for (int i = 0; i < getText().length(); i++) {
            gaps[i] = getPaint().measureText(getText().charAt(i) + "");
        }

        for (int i = 0; i < oldText.length(); i++) {
            oldGaps[i] = oldPaint.measureText(oldText.charAt(i) + "");
        }

        final Rect bounds = copyBounds();
        oldStartX = bounds.left + (bounds.width() - oldPaint.measureText(oldText.toString())) / 2f;
        startX = bounds.left + (bounds.width() - getPaint().measureText(getText())) / 2f;
        startY = getRawBaseline();

        differentList.clear();
        differentList.addAll(CharacterUtils.diff(oldText, getText()));
    }

    @Override
    public void draw(Canvas canvas) {
        drawFrame(canvas);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        super.setColorFilter(cf);
        getOldPaint().setColorFilter(cf);
    }

    @Override
    protected int animateAlphaF(float ratio, boolean fullAlpha) {
        getOldPaint().setAlpha(super.animateAlphaF(ratio, fullAlpha));
        return getOldPaint().getAlpha();
    }

    @Override
    public void setAlpha(int alpha) {
        super.setAlpha(alpha);
        getOldPaint().setAlpha(alpha);
    }

    @Override
    public void setOutline(int color) {
        super.setOutline(color);
        getOldPaint().setShadowLayer(10, 0, 0, color);
    }

    @Override
    protected int animateColor(int color) {
        super.animateColor(color);
        getOldPaint().setColor(color);
        return color;
    }

    @Override
    public void setStyle(Paint.Style style) {
        super.setStyle(style);
        getOldPaint().setStyle(style);
    }

    @Override
    public void setTextSize(float size) {
        super.setTextSize(size);
        getOldPaint().setTextSize(size);
    }

    @Override
    public void setTypeFace(Typeface font) {
        super.setTypeFace(font);
        getOldPaint().setTypeface(font);
    }

    /**
     * Begin animation alongside draw call
     *
     * @param text
     */
    protected abstract Animator animate(CharSequence text, View parent);

    /**
     * Prepare animation before drawing
     *
     * @param text
     */
    protected abstract void animatePrepare(CharSequence text);

    /**
     * Draw a frame. Called from onDraw typically
     *
     * @param canvas
     */
    protected abstract void drawFrame(Canvas canvas);

}

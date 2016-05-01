package com.fleksy.textdrawable;

import android.graphics.Color;
import android.graphics.ColorFilter;
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

    private int id = 0;
    private int currentColor = 0;
    private boolean display = true;
    private final Rect currBounds = new Rect();
    private final TextPaint paint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);

    /**
     * The base class for all custom drawables.
     * Provides the necessary TextPaint,
     * current Rect bounds for easy access,
     * integer ID should you need to tag them,
     * current color integer to avoid calling paint natively,
     * and a display flag should you need to "hide" or "show" the drawable on demand.
     */
    public BaseDrawable() {
        this(0, Style.FILL);
    }

    public BaseDrawable(int color, Style style) {
        setStyle(style);
        setColor(color);
        paint.setAntiAlias(true);
        paint.setSubpixelText(true);
    }

    /**
     * @return The int ID set to the drawable
     */
    public final int getId() {
        return id;
    }

    /**
     * Update/Set the drawables' current ID
     *
     * @param id the new ID for the drawable
     */
    public final void setId(int id) {
        this.id = id;
    }

    /**
     * Called after onBoundsChange(), appropriate place to make any UI changes as the bounds
     * have been updated. Update your paths, rects, whatever you think is necessary when
     * the bounds have been changed.
     *
     * Note, getCurrentBounds() will return the OLD bounds as it becomes updated after this
     * method has finished. The bounds Rect passed is the NEW/UPDATED bounds.
     *
     * @param bounds the current bounds of the drawable
     */
    protected abstract void createPath(Rect bounds);

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        createPath(bounds);
        currBounds.set(bounds);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        paint.setColorFilter(cf); // Great for changing all the non-transparent bits one color!
    }

    @Override
    public int getOpacity() {
        return 0; // Never found a use for this. Can also seriously harm performance if specified.
    }

    @Override
    public void setAlpha(int alpha) {
        onCancelAnimations();
        paint.setAlpha(alpha);
    }

    @Override
    public ConstantState getConstantState() { // Please don't use this with bitmaps :(
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

    /**
     * Returns the current bounds without having to use copyBounds() or creating a new Rect()
     *
     * @return A Rect containing the current bounds.
     */
    public Rect getCurrentBounds() {
        return currBounds;
    }

    /**
     * Helper method for setting the bounds with float. Note that this will Math.round() all
     * dimensions being passed. Not a fan? Cast the floats to integers!
     *
     * @param left the left side of the bounds
     * @param top the top side of the bounds
     * @param right the right side of the bounds
     * @param bottom the bottom side of the bounds
     */
    public void setBounds(float left, float top, float right, float bottom) {
        setBounds(Math.round(left), Math.round(top), Math.round(right), Math.round(bottom));
    }

    /**
     * Helper method for setting the bounds with a RectF(). Note that this will Math.round() all
     * dimensions being passed. Not a fan? ...well shoot, maybe you should've used a Rect() instead.
     *
     * @param bounds RectF bounds to update the drawable by
     */
    public void setBounds(RectF bounds) {
        setBounds(bounds.left, bounds.top, bounds.right, bounds.bottom);
    }

    /**
     * Updates the style of the TextPaint. Like FILL, STROKE, and FILL_AND_STROKE.
     *
     * @param style The style to update the paint. Default style is already FILL.
     */
    public void setStyle(Style style) {
        paint.setStyle(style);
    }

    /**
     * Should you need to "hide" or "show" your drawable, this method will tell you when to
     * ignore onDraw calls. Set the flag with "setVisibility()"
     *
     * @return True if the drawable can be rendered, otherwise you should ignore the onDraw() call
     */
    public boolean isShown() {
        return display;
    }

    /**
     * Updates the visibility of the drawable. The current value can be found from the isShown()
     * method. (Note, you have to handle the visibility yourself by checking the current value
     * of the isShown() state in your onDraw() call (or elsewhere, I dunno, I'm not making your app))
     *
     * @param display True if you want the drawable to be rendered, False otherwise. Default is True
     */
    public void setVisibility(boolean display) {
        this.display = display;
    }

    /**
     * Get the current TextPaint object being used. (Be careful when editing color and alpha as this
     * will not update the values stored in the BaseDrawable class)
     *
     * @return the TextPaint
     */
    protected TextPaint getPaint() {
        return paint;
    }

    /**
     * Returns the current color of the paint (including if it's being animated)
     *
     * @return current color of the Drawables' paint object
     */
    public int getColor() {
        return currentColor;
    }

    /**
     * Updates the outline of the paint element (ya know, the outline that gives text and drawables
     * a sort of shadow below them. Seriously, check it out if you need shadows!). Feel free to
     * override as I know these magic numbers might not be what you're looking for. This will cancel
     * any animations currently running (animation support for setShadow() is not currently provided)
     *
     * @param color the color that will be applied to the outline
     */
    public void setShadow(int color) {
        onCancelAnimations();
        paint.setShadowLayer(10, 0, 0, color); // Looks really freakin' sweet...or on Fleksy it does
    }

    /**
     * Sets the color of the paint element along with cancelling any animations currently running.
     * If you prefer to animate the color, please extend the class and call animateColor() when
     * necessary.
     *
     * @param color the new color for the paint element
     */
    public final void setColor(int color) {
        onCancelAnimations();
        animateColor(color);
    }

    /**
     * Helper method for animating the color. Will update the currentColor variable and apply the
     * new color to the Paint element.
     *
     * @param color the new color for the paint element
     * @return the color integer the paint has been animated into (for convenience)
     */
    protected int animateColor(int color) {
        currentColor = color;
        paint.setColor(color);
        return currentColor;
    }

    /**
     * @return the current integer alpha value of the paint element (0 - 255)
     */
    public int getAlpha() {
        return paint.getAlpha();
    }

    /**
     * Check if the current bounds of the drawable contain the specified point. Great for tap detection!
     *
     * @param x x-coordinate
     * @param y y-coordinate
     * @return True if the coordinates are within the drawables' bounds, otherwise False
     */
    public final boolean contains(float x, float y) {
        return new RectF(currBounds).contains(x, y);
    }

    /**
     * Helper method for all those times (aka all the time) you need the float value of the alpha,
     * but since a Paint element provides an integer between 0 - 255 you gotta do math. This method
     * takes care of all of it for you.
     *
     * @return the float based on the paint's current alpha (basically paint.getAlpha()/255)
     */
    public final float getAlphaF() { // I was using Math.min(1.0f, etc...) originally, but this looks safer
        return Math.max(0.0f, (paint.getAlpha() * 1.0f) / (CharacterUtils.MAX_ARGB * 1.0f));
    }

    /**
     * Sets the drawables' alpha by using a float (between 0 - 1) which directly corresponds with
     * (0 - 255). Now, this could either completely ignore the current alpha of the color, or
     * respect the current color's alpha and set alpha appropriately. This will also cancel any
     * animations currently running. To avoid this, please extend the class and call animateAlphaF()
     *
     * @param alpha the new alpha value for the paint element (must be between 0 - 1)
     * @param respectCurrentColor flag to determine whether we will change the alpha with respect
     *                            to the current color's alpha
     */
    public final void setAlphaF(float alpha, boolean respectCurrentColor) {
        onCancelAnimations();
        animateAlphaF(alpha, respectCurrentColor);
    }

    /**
     * Animates the drawables alpha using a float (between 0 - 1) which directly corresponds with
     * (0 - 255). Now, this could either completely ignore the current alpha of the color, or
     * respect the current color's alpha and set alpha appropriately.
     *
     * @param alpha the new alpha value for the paint element (must be between 0 - 1)
     * @param respectCurrentColor flag to determine whether we will change the alpha with respect
     *                            to the current color's alpha
     * @return an integer of the animated alpha (0 - 255) for convenience
     */
    protected int animateAlphaF(float alpha, boolean respectCurrentColor) {
        final int colorAlpha = respectCurrentColor ? CharacterUtils.MAX_ARGB : Color.alpha(currentColor);
        final int paintAlpha = (int) (alpha * colorAlpha);
        paint.setAlpha(paintAlpha);
        return paintAlpha;
    }

    /**
     * Helper method that will provide necessary callbacks as to when you should cancel any
     * animations that will affect the state of the drawable (color, alpha, transformation, etc..)
     */
    protected void onCancelAnimations() {
        //TODO: Override where necessary. Great for animations!
    }

}

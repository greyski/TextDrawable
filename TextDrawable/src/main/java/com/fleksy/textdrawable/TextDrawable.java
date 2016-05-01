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

    private CharSequence currentText = "";

    private boolean includeFontSpacing = false;

    private float textSize = 0;
    private float textScale = 1;
    private float centerX = 0, centerY = 0;
    private float translateX = 0, translateY = 0;

    private StaticLayout staticTextLayout;
    private Alignment textAlignment = Alignment.ALIGN_CENTER;

    public TextDrawable() {
        this(null);
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

    /**
     * TextDrawable class running with all of the functionality from BaseDrawable.
     * Capable of rendering emojis and unicode characters, but leaves the sizing, alignment, and
     * general layout to the developer, yet provides the light-weight
     *
     * @param text Text to display in the layout
     * @param typeface Typeface to render for the text displayed
     * @param size Size of the text to display
     * @param color Color of the text in the layout
     * @param shadowColor Provides a shadow around the text
     */
    public TextDrawable(String text, Typeface typeface, float size, int color, int shadowColor) {
        super(color, Paint.Style.FILL);
        this.currentText = text == null ? "" : text;
        setTypeFace(typeface);
        if (shadowColor != 0) {
            setShadow(shadowColor);
        }
        setTextSize(size);
        setDefaultBounds();
    }

    /**
     * Constructs a static layout that handles all text rendering at the lowest level
     *
     * @param bounds Primarily used for width sizing as height is determined by text size
     */
    private void createLayout(Rect bounds) {
        staticTextLayout = new StaticLayout(currentText, getPaint(),
                Math.max(bounds.width(), 0),
                textAlignment, 1.0f, 0.0f, false);
    }

    @Override
    protected void createPath(Rect bounds) {
        centerX = bounds.exactCenterX();
        centerY = bounds.exactCenterY();
        switch (getPaint().getTextAlign()) {
            case RIGHT:
                setTranslationX(bounds.width() / 2);
                break;
            case LEFT:
                setTranslationX(-bounds.width() / 2);
                break;
            default:
                setTranslationX(0);
                break;
        }
        createLayout(bounds);
    }

    @Override
    public void draw(Canvas canvas) {
        if (isShown() && staticTextLayout != null) {
            canvas.save();
            canvas.translate(centerX + translateX, getBaseline());
            staticTextLayout.draw(canvas);
            canvas.restore();
        }
    }

    /**
     * @return The current text
     */
    public String getText() {
        return currentText.toString();
    }

    /**
     * Sets the CharSequence of text to be displayed
     *
     * @param text update to the current text
     * @return True of the text was updated, False if not
     */
    public boolean setText(CharSequence text) {
        return setText((String) text);
    }

    /**
     * Sets the String of text to be displayed. Handles null.
     *
     * @param text update to the current text
     * @return True of the text was updated, False if not
     */
    public boolean setText(String text) {
        if (text == null) {
            text = "";
        }
        final boolean changedText = !this.currentText.equals(text);
        this.currentText = text;
        if (changedText || staticTextLayout == null) {
            createLayout(getCurrentBounds());
        }
        return changedText;
    }

    /**
     * @return The current text size
     */
    public float getTextSize() {
        return textSize;
    }

    /**
     * Set the paint's text size. This value must be > 0
     *
     * @param size set the paint's text size.
     */
    public void setTextSize(float size) {
        textSize = size;
        getPaint().setTextSize(size);
    }

    /**
     * Set or clear the typeface object.
     * Pass null to clear any previous typeface.
     * As a convenience, the parameter passed is also returned.
     *
     * @param font May be null. The typeface to be installed in the paint. Also named it font
     *             to bother nitpickers
     */
    public void setTypeFace(Typeface font) {
        getPaint().setTypeface(font);
    }

    /**
     * Get the paint's typeface object.
     * The typeface object identifies which font to use when drawing or
     * measuring text.
     *
     * @return the paint's typeface (or null)
     */
    public Typeface getTypeFace() {
        return getPaint().getTypeface();
    }

    /**
     * Translates the x-coordinate of the text.
     *
     * @param x to translate the text horizontally
     */
    public void setTranslationX(float x) {
        translateX = x;
    }

    /**
     * Translate the y-coordinate of the text.
     *
     * @param y to translate the text vertically
     */
    public void setTranslationY(float y) {
        translateY = y;
    }

    /**
     * @return Provides the artificial location of where the glyphs align along the bottom.
     */
    public float getBaseline() {
        return centerY + getYPositioning(getPaint(), textScale, textSize) + translateY;
    }

    /**
     * @return Return the vertical position of the baseline of the lowest line.
     */
    public float getBottomBaseLine() {
        return staticTextLayout.getLineBaseline(staticTextLayout.getLineCount() - 1) + getBaseline();
    }

    /**
     * Set the text alignement.
     *
     * @param align Options are CENTER, RIGHT, LEFT. Default is CENTER
     */
    public void setTextAlign(Alignment align) {
        this.textAlignment = align;
    }

    /**
     * @return The minimal possible bounds for the text to be laid out.
     */
    public Rect getDefaultBounds() {
        return defaultBounds(getPaint(), currentText.toString(),
                textScale, textSize, includeFontSpacing);
    }

    /**
     * Automatically sets the layout to the minimum bounds in order for the text to be displayed.
     * This assumes that the coordinates 0,0 (x,y) are viable.
     *
     * @return The default bounds the text was set to.
     */
    public Rect setDefaultBounds() {
        final Rect bounds = getDefaultBounds();
        setBounds(bounds);
        return bounds;
    }

    /**
     * Some typefaces have added spacing between letters both horizontally and vertically.
     *
     * @param includeFontSpacing True if you want the spacing to be handled, False to ignore the spacing
     */
    public void setIncludeFontSpacing(boolean includeFontSpacing) {
        this.includeFontSpacing = includeFontSpacing;
    }

    /**
     * @return True if the current text has multiple lines of text (new line commands), False otherwise
     */
    public boolean isMultiline() {
        return currentText.toString().split("\r\n|\r|\n").length > 1;
    }

    /**
     * @return The width of the text calculated by it's Paint element.
     */
    public int getTextWidth() {
        final Rect bounds = new Rect();
        getPaint().getTextBounds(currentText.toString(), 0,
                currentText.toString().length(), bounds);
        return bounds.width();
    }

    public boolean isBold() {
        return getPaint().isFakeBoldText();
    }

    public void setBold(boolean bold) {
        getPaint().setFakeBoldText(bold);
    }

    /**
     * @return The current scale of the text, which is 1 by default
     */
    public float getTextScale() {
        return textScale;
    }

    /**
     * @param scale will scale the text
     */
    public void setTextScale(float scale) {
        textScale = scale;
    }

    /**
     * This is some beautiful/ugly method that provides the perfect y-coordinate that's positioned
     * at the center of the text layout bounds. Oh the time I spent trying to figure this out...
     * I can still feel the tears on my cheek
     *
     * @param paint Primary component with rendering the text
     * @param textScale Scale desired for the text
     * @param textSize Size desired for the text
     * @return If you could imagine a line that evenly goes through the text horizontally, that's the
     * coordinate this returns
     */
    private static int getYPositioning(TextPaint paint, float textScale, float textSize) {
        paint.setTextSize(textSize * textScale); // Updates the paint the appropriate text size vs scale
        final int in = Math.round( // Better to round up
                (paint.ascent() - paint.descent()) // Returns the difference between the ascent and descent
                        / (textScale * 2.0f) // This has to do with us finding the y-coordinate halfway between the top and bottom
        );
        return (int) (in / textScale);
    }

    /**
     * @param paint Primary object for housing all UI rendering utilities/variables/what-not
     * @param text String of text to be displayed
     * @param scaleText Scale for the text
     * @param sizeText Size for the text
     * @param includeFontSpacing True/False as to whether text spacing should be included in the caluclations
     * @return The minimal Rect bounds needed to display the text.
     */
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

    /**
     * @param text String of text to be sized
     * @param sizeText Size to render the text by
     * @return The default Rect bounds that will accommodate the text at the desired size
     */
    public static Rect defaultBounds(String text, float sizeText) {
        final TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(sizeText);
        return defaultBounds(paint, text, 1, sizeText, true);
    }


    /**
     * Very expensive method for providing the optimal text size in order to fit the text
     * within the given parent width/height.
     *
     * @param text String of text to be sized
     * @param parentW Width of the parent
     * @param parentH Height of the parent
     * @param targetTextSize Desired text size to obtain
     * @return The optimal text size which can be less than the targetTextSize or 0 if it's not possible
     */
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

    /**
     * Very expensive.
     *
     * @return True if the text fits within the given parent width/height with the given textSize
     */
    private static boolean fitsParent(final String text, final TextPaint paint,
                                      final float parentW, final float parentH, float textSize) {
        paint.setTextSize(textSize);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        StaticLayout textLayout = new StaticLayout(text, paint, bounds.width(), Alignment.ALIGN_NORMAL, 0.0f, 0.0f, false);
        return (textLayout.getHeight() <= parentH && textLayout.getWidth() <= parentW) &&
                (Math.abs(parentH - textLayout.getHeight()) >= 0 &&
                        Math.abs(parentW - textLayout.getWidth()) >= 0);
    }

}

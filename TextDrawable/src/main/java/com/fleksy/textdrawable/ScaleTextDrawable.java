package com.fleksy.textdrawable;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Stolen & Edited by Fleksy on 2/24/16.
 * Editor Greyski.
 */
public final class ScaleTextDrawable extends AnimationTextDrawable {

    float mostCount = 20;
    final float msPerChar;
    private long duration;
    private float progress;

    public ScaleTextDrawable(float msPerChar) {
        super();
        this.msPerChar = msPerChar;
    }

    @Override
    protected Animator animate(CharSequence text, final View parent) {
        int n = getText().length();
        n = n <= 0 ? 1 : n;

        duration = (long) (msPerChar + msPerChar / mostCount * (n - 1));

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, duration).setDuration(duration);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                progress = (float) animation.getAnimatedValue();
                parent.invalidate();
            }
        });
        return valueAnimator;
    }

    @Override
    protected void animatePrepare(CharSequence text) {

    }

    @Override
    protected void drawFrame(Canvas canvas) {
        float offset = startX;
        float oldOffset = oldStartX;

        int maxLength = Math.max(getText().length(), oldText.length());

        for (int i = 0; i < maxLength; i++) {

            // draw old text
            if (i < oldText.length()) {

                float percent = progress / duration;
                int move = CharacterUtils.needMove(i, differentList);
                if (move != -1) {
                    getOldPaint().setTextSize(getTextSize());
                    getOldPaint().setAlpha(255);

                    float p = percent * 2f;
                    p = p > 1 ? 1 : p;
                    float distX = CharacterUtils.getOffset(i, move, p, startX, oldStartX, gaps, oldGaps);
                    canvas.drawText(oldText.charAt(i) + "", 0, 1, distX, startY, getOldPaint());
                } else {
                    getOldPaint().setAlpha((int) ((1 - percent) * 255));
                    getOldPaint().setTextSize(getTextSize() * (1 - percent));
                    float width = getOldPaint().measureText(oldText.charAt(i) + "");
                    canvas.drawText(oldText.charAt(i) + "", 0, 1, oldOffset + (oldGaps[i] - width) / 2, startY, getOldPaint());
                }
                oldOffset += oldGaps[i];
            }

            // draw new text
            if (i < getText().length()) {

                if (!CharacterUtils.stayHere(i, differentList)) {

                    int alpha = (int) (255f / msPerChar * (progress - msPerChar * i / mostCount));
                    if (alpha > 255) alpha = 255;
                    if (alpha < 0) alpha = 0;

                    float size = getTextSize() * 1f / msPerChar * (progress - msPerChar * i / mostCount);
                    if (size > getTextSize()) size = getTextSize();
                    if (size < 0) size = 0;

                    paint.setAlpha(alpha);
                    paint.setTextSize(size);

                    float width = paint.measureText(getText().charAt(i) + "");
                    canvas.drawText(getText().charAt(i) + "", 0, 1, offset + (gaps[i] - width) / 2, startY, paint);
                }

                offset += gaps[i];
            }
        }
    }

}

package com.fleksy.textdrawable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Edited by Fleksy on 2/24/16.
 * Editor Greyski.
 * Author hanks on 15-12-14.
 */
class CharacterUtils {

    /**
     * Maximum ARGB value as A, R, G, B top out at 255
     */
    public final static int MAX_ARGB = 255;

    /**
     * Find differences between two Strings and make a list of the CharacterDiffResults
     * for animations
     *
     * @param oldText
     * @param newText
     * @return
     */
    public static List<CharacterDiffResult> diff(CharSequence oldText, CharSequence newText) {

        List<CharacterDiffResult> differentList = new ArrayList<>();
        Set<Integer> skip = new HashSet<>();

        for (int i = 0; i < oldText.length(); i++) {
            char c = oldText.charAt(i);
            for (int j = 0; j < newText.length(); j++) {
                if (!skip.contains(j) && c == newText.charAt(j)) {
                    skip.add(j);
                    CharacterDiffResult different = new CharacterDiffResult();
                    different.c = c;
                    different.fromIndex = i;
                    different.moveIndex = j;
                    differentList.add(different);
                    break;
                }
            }
        }
        return differentList;
    }

    /**
     * Find the index that needs to get moved from the specified passed index
     *
     * @param index
     * @param differentList
     * @return
     */
    public static int needMove(int index, List<CharacterDiffResult> differentList) {
        for (CharacterDiffResult different : differentList) {
            if (different.fromIndex == index) {
                return different.moveIndex;
            }
        }
        return -1;
    }

    /**
     * Determine whether the character at the specified index needs to remain in place
     *
     * @param index
     * @param differentList
     * @return
     */
    public static boolean stayHere(int index, List<CharacterDiffResult> differentList) {
        for (CharacterDiffResult different : differentList) {
            if (different.moveIndex == index) {
                return true;
            }
        }
        return false;
    }

    /**
     * Find offset needed to move characters for their animation
     *
     * @param from
     * @param move
     * @param progress
     * @param startX
     * @param oldStartX
     * @param gaps
     * @param oldGaps
     * @return
     */
    public static float getOffset(int from, int move, float progress, float startX, float oldStartX, float[] gaps, float[] oldGaps) {

        float dist = startX;
        for (int i = 0; i < move; i++) {
            dist += gaps[i];
        }

        float cur = oldStartX;
        for (int i = 0; i < from; i++) {
            cur += oldGaps[i];
        }

        return cur + (dist - cur) * progress;

    }

    /**
     * Realign text to match language preferences
     *
     * @param alignMe
     * @param rtlLanguage
     * @return
     */
    public static String getAlignedText(String alignMe, boolean rtlLanguage) {
        return rtlLanguage ? new StringBuilder(alignMe).reverse().toString() : alignMe;
    }

    /**
     * Character, the original index it was located, and the index it will move to
     */
    public static class CharacterDiffResult {
        public char c;
        public int fromIndex;
        public int moveIndex;
    }

}

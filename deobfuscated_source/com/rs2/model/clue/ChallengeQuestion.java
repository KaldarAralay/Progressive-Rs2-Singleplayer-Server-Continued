/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.clue;

import java.util.HashMap;
import java.util.Map;

public enum ChallengeQuestion {
    a(new String[]{"What is 57x89+23?"}, 2831, 2842, 5096),
    b(new String[]{"What is 19 to the power of 3?"}, 2837, 2844, 6859),
    c(new String[]{"How many fishermen are there on", "the fishing platform?"}, 2848, 2846, 11),
    d(new String[]{"If x is 15 and y is 3,", " what is 3x + y?"}, 2849, 2850, 48),
    e(new String[]{"How many people are waiting for", "the next Bard to perform?"}, 2853, 2852, 4),
    f(new String[]{"How many animals are in", "the Ardougne Zoo?"}, 2855, 2854, 40),
    g(new String[]{"How many buildings are there in", "the village?"}, 2858, 7275, 11),
    h(new String[]{"How many bookcases are there ", "in the palace library?"}, 3607, 7277, 24),
    i(new String[]{"How many pigeon cages are there ", "around the back of Jerico's house?"}, 3610, 7279, 3),
    j(new String[]{"How many cannons does Lumbridge", "castle have?"}, 3611, 7281, 9),
    k(new String[]{"I have 16 kebabs, I eat one myself and", "share the rest equally between 3 friends.", "How many do they have each?"}, 3613, 7283, 5),
    l(new String[]{"How many flowers are there in", "the clearing below this platform?"}, 3566, 7269, 13),
    m(new String[]{"How many gnomeballers have", "red patches on their uniforms?"}, 3568, 7271, 6),
    n(new String[]{"How many banana trees are there", "in the plantation?"}, 3570, 7273, 33);

    private String[] questionLines;
    private int clueItemId;
    private int answerItemId;
    private int answerValue;
    private static Map questionsByClueItemId;
    private static Map questionsByAnswerItemId;

    static {
        questionsByClueItemId = new HashMap();
        questionsByAnswerItemId = new HashMap();
        ChallengeQuestion[] challengeQuestionArray = ChallengeQuestion.values();
        int n = challengeQuestionArray.length;
        int n2 = 0;
        while (n2 < n) {
            ChallengeQuestion challengeQuestion = challengeQuestionArray[n2];
            questionsByClueItemId.put(challengeQuestion.clueItemId, challengeQuestion);
            questionsByAnswerItemId.put(challengeQuestion.answerItemId, challengeQuestion);
            ++n2;
        }
    }

    public static ChallengeQuestion forClueItemId(int n) {
        return (ChallengeQuestion)((Object)questionsByClueItemId.get(n));
    }

    public static ChallengeQuestion forAnswerItemId(int n) {
        return (ChallengeQuestion)((Object)questionsByAnswerItemId.get(n));
    }

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    private ChallengeQuestion(String[] stringArray, int n2, int n3, int n4) {
        this.questionLines = stringArray;
        this.clueItemId = n2;
        this.answerItemId = n3;
        this.answerValue = n4;
    }

    public final String[] getQuestionLines() {
        return this.questionLines;
    }

    public final int getClueItemId() {
        return this.clueItemId;
    }

    public final int getAnswerItemId() {
        return this.answerItemId;
    }

    public final int getAnswerValue() {
        return this.answerValue;
    }
}


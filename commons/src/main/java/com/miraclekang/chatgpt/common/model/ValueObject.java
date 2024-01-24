package com.miraclekang.chatgpt.common.model;

import java.io.Serializable;

public class ValueObject implements Serializable {

    public static <T extends Comparable<T>> T max(T a, T b) {
        if (a.compareTo(b) > 0) {
            return a;
        }
        return b;
    }

    public static <T extends Comparable<T>> T min(T a, T b) {
        if (a.compareTo(b) < 0) {
            return a;
        }
        return b;
    }
}

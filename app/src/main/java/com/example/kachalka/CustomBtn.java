package com.example.kachalka;

import android.widget.TextView;

import java.util.Dictionary;

public class CustomBtn {
    public static String onClick(Dictionary<String, String> info, String selected) {
        String value = info.get(selected);
        return value;
    }
}

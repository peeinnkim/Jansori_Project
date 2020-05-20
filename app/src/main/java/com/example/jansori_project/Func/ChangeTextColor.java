package com.example.jansori_project.Func;

import android.graphics.Color;
import android.provider.CalendarContract;

import static android.graphics.Color.blue;
import static android.graphics.Color.green;
import static android.graphics.Color.red;

public class ChangeTextColor {

    public String chagneTextCOlor(int bgColor) {
        int threshold = 105;
        int[] components = getRGBColor(bgColor);
        double bgDelta = (components[0] * 0.299) + (components[1] * 0.587) + (components[2] * 0.114); //명도값 구하기

        return ((255 - bgDelta) < threshold) ? "#000000" : "#ffffff";
    }//chagneTextCOlor

    public int[] getRGBColor(int color) {

        int r = red(color);
        int g = green(color);
        int b = blue(color);

        int[] colorRGB = new int[]{r, g, b};

        return colorRGB;
    }//getRGBColor

}//ChangeTextColor

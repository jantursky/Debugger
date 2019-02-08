package com.jantursky.debugger.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class SizeUtils {

    public static String getDataSize(float floatDataSize) {
        String output;
        if (floatDataSize > 1000000) {
            float size = floatDataSize / 1000000f;
            DecimalFormat df = new DecimalFormat("###.##");
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setDecimalSeparator(',');
            df.setDecimalFormatSymbols(symbols);
            output = df.format(size) + " MB";
        } else if (floatDataSize > 1000) {
            float size = floatDataSize / 1000f;
            DecimalFormat df = new DecimalFormat("###.####");
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setDecimalSeparator(',');
            df.setDecimalFormatSymbols(symbols);
            output = df.format(size) + " kB";
        } else {
            output = floatDataSize + " B";
        }
        return output;
    }

}
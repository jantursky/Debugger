package com.jantursky.debugger.utils;

public class FormatUtils {

    public static String formatSchema(String schema) {
        if (!StringUtils.isEmpty(schema)) {
            schema = schema.replaceAll("\\(", "\n(");
            schema = schema.replaceAll("\\)", "\n)");
            schema = schema.replaceAll(",", ",\n\t");
        }
        return schema;
    }
}

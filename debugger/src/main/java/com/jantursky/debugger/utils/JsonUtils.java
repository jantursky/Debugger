package com.jantursky.debugger.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {

    private static final String TAG = JsonUtils.class.getSimpleName();

    public static boolean isJSONObjectValid(String test) {
        try {
            new JSONObject(test);
            return true;
        } catch (JSONException ex) {
//            ex.printStackTrace();
        }
        return false;
    }

    public static boolean isJSONArrayValid(String test) {
        try {
            new JSONArray(test);
            return true;
        } catch (JSONException ex) {
//            ex.printStackTrace();
        }
        return false;
    }

    public static String getIndentedText(String text) {
        if (isJSONObjectValid(text)) {
            try {
                return new JSONObject(text).toString(3);
            } catch (JSONException e) {
//                e.printStackTrace();
            }
        } else if (isJSONArrayValid(text)) {
            try {
                return new JSONArray(text).toString(3);
            } catch (JSONException e) {
//                e.printStackTrace();
            }
        }
        return text;
    }
}
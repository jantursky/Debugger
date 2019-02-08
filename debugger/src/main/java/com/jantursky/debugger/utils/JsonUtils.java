package com.jantursky.debugger.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

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
        return getIndentedText(text, false);
    }

    public static String getIndentedText(String text, boolean sortAlphabetically) {
        if (isJSONObjectValid(text)) {
            try {
                JSONObject jsonObject = new JSONObject(text);
                if (sortAlphabetically) {
                    jsonObject = sortAlphabetically(jsonObject);
                }
                return jsonObject.toString(3);
            } catch (JSONException e) {
//                e.printStackTrace();
            }
        } else if (isJSONArrayValid(text)) {
            try {
                JSONArray jsonArray = new JSONArray(text);
                if (sortAlphabetically) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        if (jsonArray.opt(i) instanceof JSONObject) {
                            jsonArray.put(i, sortAlphabetically((JSONObject) jsonArray.opt(i)));
                        }
                    }
                }
                return jsonArray.toString(3);
            } catch (JSONException e) {
//                e.printStackTrace();
            }
        }
        return text;
    }

    private static JSONObject sortAlphabetically(JSONObject jsonObject) {
        List<String> jsonKeys = new ArrayList<>();
        Iterator<String> keys = jsonObject.keys();

        while (keys.hasNext()) {
            String key = keys.next();
            if (jsonObject.opt(key) instanceof JSONObject) {
                try {
                    jsonObject.put(key, sortAlphabetically((JSONObject) jsonObject.opt(key)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (jsonObject.opt(key) instanceof JSONArray) {
                try {
                    JSONArray jsonArray = (JSONArray) jsonObject.opt(key);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        if (jsonArray.opt(i) instanceof JSONObject) {
                            jsonArray.put(i, sortAlphabetically((JSONObject) jsonArray.opt(i)));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            jsonKeys.add(key);
        }

        Collections.sort(jsonKeys);
        JSONObject sortedJsonObject = new JSONObject();
        for (String key : jsonKeys) {
            try {
                sortedJsonObject.put(key, jsonObject.opt(key));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sortedJsonObject;
    }
}
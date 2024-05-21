package org.se13.utils;

import org.json.JSONArray;
import org.json.JSONObject;

public class JsonUtils {

    public static double[][] getDoubleArray(JSONObject jsonObject, String key) {
        JSONArray jsonArray = jsonObject.getJSONArray(key);
        int rows = jsonArray.length();
        int cols = jsonArray.getJSONArray(0).length();
        double[][] result = new double[rows][cols];

        for (int i = 0; i < rows; i++) {
            JSONArray row = jsonArray.getJSONArray(i);
            for (int j = 0; j < cols; j++) {
                result[i][j] = row.getDouble(j);
            }
        }

        return result;
    }
}

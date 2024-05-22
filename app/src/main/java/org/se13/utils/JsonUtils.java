package org.se13.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.se13.ai.Computer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonUtils {

    public static float[][] getFloatArray(JSONObject jsonObject, String key) {
        JSONArray jsonArray = jsonObject.getJSONArray(key);
        int rows = jsonArray.length();
        int cols = jsonArray.getJSONArray(0).length();
        float[][] result = new float[rows][cols];

        for (int i = 0; i < rows; i++) {
            JSONArray row = jsonArray.getJSONArray(i);
            for (int j = 0; j < cols; j++) {
                result[i][j] = row.getFloat(j);
            }
        }

        return result;
    }

    public static JSONObject readJson() {
        try {
            String content = new String(Files.readAllBytes(Paths.get(path)));
            return new JSONObject(content);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public static void saveJson(JSONObject object) throws IOException {
        FileWriter fs = new FileWriter(path);
        BufferedWriter writer = new BufferedWriter(fs);
        object.write(writer);
        writer.close();
    }

    public static final String path = "C:/Users/someh/Downloads/computer.json";

    public static Computer.SaveComputer saver = (computerId, w1, w2, w3, w4, fitness) ->
        new Thread(() -> {
            try {
                JSONObject parent = new JSONObject();
                JSONObject object = createObject(w1, w2, w3, w4, fitness);
                parent.put(String.valueOf(computerId), object);
                saveJson(parent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    public static JSONObject createObject(float[][] w1, float[][] w2, float[][] w3, float[][] w4, float fitness) {
        JSONObject object = new JSONObject();
        object.put("w1", new JSONArray(w1));
        object.put("w2", new JSONArray(w2));
        object.put("w3", new JSONArray(w3));
        object.put("w4", new JSONArray(w4));
        object.put("fitness", fitness);

        return object;
    }

}

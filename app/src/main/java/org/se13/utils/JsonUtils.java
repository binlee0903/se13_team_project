package org.se13.utils;

import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.se13.ai.SaveData;
import org.se13.ai.NeuralResult;

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

    public static SaveData readJson() {
        try {
            String content = new String(Files.readAllBytes(Paths.get(path)));
            return gson.fromJson(content, SaveData.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public static void saveJson(SaveData data) throws IOException {
        FileWriter fs = new FileWriter(path);
        BufferedWriter writer = new BufferedWriter(fs);
        writer.write(gson.toJson(data));
        writer.close();
    }

    public static final String path = "./computer.json";

    public static String createObject(NeuralResult result) {
        return gson.toJson(result);
    }

    private static Gson gson = new Gson();
}

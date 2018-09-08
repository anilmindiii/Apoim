package com.apoim.groupchatwebrtc.utils;

import com.google.gson.Gson;

import java.io.IOException;

import com.apoim.groupchatwebrtc.models.QbConfigs;

public class ConfigUtils {
    public static QbConfigs getCoreConfigs(String fileName) throws IOException {
        ConfigParser configParser = new ConfigParser();
        Gson gson = new Gson();
        return gson.fromJson(configParser.getConfigsAsJsonString(fileName), QbConfigs.class);
    }

    public static QbConfigs getCoreConfigsOrNull(String fileName){
        QbConfigs qbConfigs = null;

        try {
            qbConfigs = getCoreConfigs(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return qbConfigs;
    }
}
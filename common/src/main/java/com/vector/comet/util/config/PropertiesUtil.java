package com.vector.comet.util.config;

import com.vector.comet.util.StringUtils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * Created by jinku on 2018/4/15.
 */
public class PropertiesUtil {

    private static Map<String, String> mergedMap = new HashMap();

    static {
        loadResource("config.properties");
    }

    private static void loadResource(String fileName) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle(fileName, Locale.getDefault());
        Enumeration<String> keys = resourceBundle.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            mergedMap.put(key, resourceBundle.getString(key));
        }
    }

    public static String getString(String key) {
        return mergedMap.get(key);
    }

    public static int getInt(String key) {
        return Integer.parseInt(mergedMap.get(key));
    }
}

package com.bloomberg.bask.util;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

public class PropertiesHelper {

    public static Properties extractConfig(String prefix, Properties props) {
        String key, value;
        Properties newProp = new Properties();
        Enumeration<?> e = props.propertyNames();
        while (e.hasMoreElements()) {
            key = (String)e.nextElement();
            if (key.startsWith(prefix)) {
                value = props.getProperty(key);
                newProp.setProperty(key.replace(prefix, ""), value);
            }
        }
        return newProp;
    }

    public static List<String> valuesToList(String value) {
        return Arrays.asList(value.split("\\s*,\\s*"));
    }
}

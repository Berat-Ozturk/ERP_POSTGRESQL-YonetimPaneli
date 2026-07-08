package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    private static final Properties props = new Properties();
    private static final String CONFIG_FILE = "config/db.properties";

    static {
        try {
            InputStream inputStream = ConfigReader.class.getClassLoader().getResourceAsStream(CONFIG_FILE);

            if (inputStream != null) {
                props.load(inputStream);
                System.out.println("✓ Config dosyası yüklendi: " + CONFIG_FILE);
            } else {
                try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
                    props.load(fis);
                    System.out.println("✓ Config dosyası yüklendi: " + CONFIG_FILE);
                }
            }
        } catch (IOException e) {
            System.err.println("✗ Config dosyası okunamadı: " + e.getMessage());
            System.err.println("Beklenen konum: " + CONFIG_FILE);
            e.printStackTrace();
        }
    }

    public static String get(String key) {
        String value = props.getProperty(key);
        if (value == null) {
            System.err.println("✗ Config anahtarı bulunamadı: " + key);
        }
        return value;
    }

    public static String get(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    public static int getInt(String key, int defaultValue) {
        String value = props.getProperty(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.err.println("✗ Geçersiz sayı formatı: " + key + " = " + value);
            }
        }
        return defaultValue;
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        String value = props.getProperty(key);
        if (value != null) {
            return Boolean.parseBoolean(value);
        }
        return defaultValue;
    }

    public static void printAll() {
        System.out.println("=== Config Değerleri ===");
        props.forEach((key, value) -> {
            if (key.toString().toLowerCase().contains("password")) {
                System.out.println(key + " = ****");
            } else {
                System.out.println(key + " = " + value);
            }
        });
    }
}
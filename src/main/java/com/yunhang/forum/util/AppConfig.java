package com.yunhang.forum.util;

import java.io.InputStream;
import java.util.Properties;

/**
 * Minimal config loader for JavaFX app (no Spring).
 * Reads /application.properties from classpath once.
 */
public final class AppConfig {
  private static final Properties PROPS = new Properties();
  private static volatile boolean loaded;

  private AppConfig() {}

  public static String get(String key) {
    if (key == null || key.isBlank()) {
      return null;
    }

    ensureLoaded();

    String sysProp = System.getProperty(key);
    if (sysProp != null && !sysProp.isBlank()) {
      return sysProp;
    }

    String val = PROPS.getProperty(key);
    return (val == null || val.isBlank()) ? null : val.trim();
  }

  private static void ensureLoaded() {
    if (loaded) {
      return;
    }

    synchronized (AppConfig.class) {
      if (loaded) {
        return;
      }

      try (InputStream in = AppConfig.class.getResourceAsStream("/application.properties")) {
        if (in != null) {
          PROPS.load(in);
        }
      } catch (Exception e) {
        LogUtil.warn("Failed to load application.properties: " + e.getMessage());
      }

      loaded = true;
    }
  }
}

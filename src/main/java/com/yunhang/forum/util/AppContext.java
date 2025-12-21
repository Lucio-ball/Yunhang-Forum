package com.yunhang.forum.util;

import com.yunhang.forum.dao.DataLoader;

/**
 * Application composition root context.
 * Keeps wiring (e.g., DataLoader implementation) out of Controllers/Services.
 */
public final class AppContext {
  private static volatile DataLoader dataLoader;

  private AppContext() {
  }

  public static void setDataLoader(DataLoader loader) {
    dataLoader = loader;
  }

  public static DataLoader getDataLoader() {
    return dataLoader;
  }
}


package com.jetbrains.python.debugger.concurrency.tool;

import java.awt.*;

public class GraphSettings {
  public static int CELL_HEIGH = 24;
  public static int NODE_WIDTH = 24;
  public static int STROKE_WITH_LOCK = 4;
  public static int STROKE_BASIC = 4;
  public static boolean USE_STD_COLORS = true;
  public static Color BASIC_COLOR = new Color(125, 125, 125);
  public static Color LOCK_WAIT_COLOR = new Color(255, 179, 3);
  public static Color LOCK_OWNING_COLOR = new Color(120, 255, 0);
  public static Color DEADLOCK_COLOR = Color.RED;
}


package com.jetbrains.python.debugger.concurrency.tool.threading;


import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ThreadingColorManager {
  private final Map<String, Color> myThreadColors;

  public ThreadingColorManager() {
    myThreadColors = new HashMap<String, Color>();
  }

  private Color generateColor() {
    Random rand = new Random();
    float r = rand.nextFloat();;
    float g = rand.nextFloat();
    float b = rand.nextFloat();
    return new Color(r, g, b);
  }

  public Color getThreadColor(String threadId) {
    if (myThreadColors.containsKey(threadId)) {
      return myThreadColors.get(threadId);
    } else {
      Color color = generateColor();
      myThreadColors.put(threadId, color);
      return color;
    }
  }
}

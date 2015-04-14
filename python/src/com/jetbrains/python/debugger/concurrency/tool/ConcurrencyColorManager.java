
package com.jetbrains.python.debugger.concurrency.tool;


import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ConcurrencyColorManager {
  private final Map<String, Color> myColors;

  public ConcurrencyColorManager() {
    myColors = new HashMap<String, Color>();
  }

  private Color generateColor() {
    Random rand = new Random();
    float r = rand.nextFloat();
    float g = rand.nextFloat();
    float b = rand.nextFloat();
    return new Color(r, g, b);
  }

  public Color getItemColor(String itemId) {
    if (myColors.containsKey(itemId)) {
      return myColors.get(itemId);
    } else {
      Color color = generateColor();
      myColors.put(itemId, color);
      return color;
    }
  }
}

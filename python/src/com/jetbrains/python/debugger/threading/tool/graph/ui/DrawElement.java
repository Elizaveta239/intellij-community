
package com.jetbrains.python.debugger.threading.tool.graph.ui;

import java.awt.*;

public class DrawElement {

  public enum ElementType {
    EMPTY, SIMPLE, EVENT, THREAD_START, THREAD_FINISH
  }

  private final ElementType myType;
  private final Color myColor;

  public DrawElement(ElementType type, Color color) {
    myType = type;
    myColor = color;
  }

  public ElementType getType() {
    return myType;
  }

  public Color getColor() {
    return myColor;
  }

  @Override
  public String toString() {
    return myType.toString();
  }
}

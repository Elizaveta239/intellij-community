
package com.jetbrains.python.debugger.threading.tool.graph.ui;

import com.jetbrains.python.debugger.threading.tool.graph.GraphSettings;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public abstract class DrawElement {

  protected Color myColor;

  public DrawElement(Color color) {
    myColor = color;
  }

  public DrawElement() {
  }

  public abstract DrawElement getNextElement();

  public void drawElement(Graphics g, int padding) {
  }

  public Color getColor() {
    return myColor;
  }

  public void setColor(Color color) {
    myColor = color;
  }

  protected void drawEvent(Graphics g, int padding) {
    if (!GraphSettings.USE_STD_COLORS) {
      g.setColor(myColor);
    }
    Graphics2D g2 = (Graphics2D)g;
    double r = GraphSettings.CELL_HEIGH * 0.25;
    double newX = Math.round((padding + 0.5f) * GraphSettings.NODE_WIDTH) - r;
    double newY = GraphSettings.CELL_HEIGH * 0.5 - r;
    RoundRectangle2D rectangle2D = new RoundRectangle2D.Double(newX, newY,
                                                               GraphSettings.CELL_HEIGH * 0.5,
                                                               GraphSettings.CELL_HEIGH * 0.5,
                                                               50, 50);
    g2.fill(rectangle2D);
  }

}

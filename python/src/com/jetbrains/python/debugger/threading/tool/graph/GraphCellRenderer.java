
package com.jetbrains.python.debugger.threading.tool.graph;


import com.intellij.ui.ColoredTableCellRenderer;
import com.jetbrains.python.debugger.threading.PyThreadingLogManager;
import com.jetbrains.python.debugger.threading.PyThreadingLogManagerImpl;
import com.jetbrains.python.debugger.threading.tool.ui.ThreadingColorManager;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

public class GraphCellRenderer extends ColoredTableCellRenderer {
  private final ThreadingColorManager myColorManager;
  private final PyThreadingLogManagerImpl myLogManager;
  private int myRow;

  public GraphCellRenderer(ThreadingColorManager colorManager, PyThreadingLogManager logManager) {
    myColorManager = colorManager;
    myLogManager = (PyThreadingLogManagerImpl)logManager;
  }

  @Override
  protected void customizeCellRenderer(JTable table, Object value, boolean selected, boolean hasFocus, int row, int column) {
    myRow = row;
    setBorder(null);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D)g;
    g2.setColor(myColorManager.getThreadColor(myLogManager.getThreadIdForEventAt(myRow)));
    RoundRectangle2D rectangle2D = new RoundRectangle2D.Double(0, 0, 20, 20, 100, 100);
    g2.fill(rectangle2D);
  }
}

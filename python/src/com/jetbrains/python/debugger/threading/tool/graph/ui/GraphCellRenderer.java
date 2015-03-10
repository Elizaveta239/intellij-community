
package com.jetbrains.python.debugger.threading.tool.graph.ui;


import com.intellij.ui.ColoredTableCellRenderer;
import com.jetbrains.python.debugger.threading.PyThreadingLogManager;
import com.jetbrains.python.debugger.threading.PyThreadingLogManagerImpl;
import com.jetbrains.python.debugger.threading.tool.graph.GraphManager;
import com.jetbrains.python.debugger.threading.tool.graph.GraphSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

public class GraphCellRenderer extends ColoredTableCellRenderer {
  private final PyThreadingLogManagerImpl myLogManager;
  private final GraphManager myGraphManager;
  private int myRow;

  public GraphCellRenderer(PyThreadingLogManager logManager, GraphManager graphManager) {
    myLogManager = (PyThreadingLogManagerImpl)logManager;
    myGraphManager = graphManager;
  }

  @Override
  protected void customizeCellRenderer(JTable table, Object value, boolean selected, boolean hasFocus, int row, int column) {
    myRow = row;
    setBorder(null);
  }

  private static void drawDrawElement(Graphics g, int padding, DrawElement element) {
    Graphics2D g2 = (Graphics2D)g;
    DrawElement.ElementType type = element.getType();
    g2.setColor(element.getColor());
    g2.setStroke(new BasicStroke(6));
    int x = Math.round((padding + 0.5f) * GraphSettings.NODE_WIDTH);

    if (type == DrawElement.ElementType.SIMPLE || type == DrawElement.ElementType.EVENT) {
      g2.drawLine(x, 0, x, GraphSettings.CELL_HEIGH);
    } else if (type == DrawElement.ElementType.THREAD_START) {
      g2.drawLine(x, Math.round(GraphSettings.CELL_HEIGH * 0.5f), x, GraphSettings.CELL_HEIGH);
    } else if (type == DrawElement.ElementType.THREAD_FINISH) {
      g2.drawLine(x, 0, x, Math.round(GraphSettings.CELL_HEIGH * 0.5f));
    }

    if (type == DrawElement.ElementType.EVENT || type == DrawElement.ElementType.THREAD_FINISH ||
        type == DrawElement.ElementType.THREAD_START) {
      double r = GraphSettings.CELL_HEIGH * 0.25;
      double newX = Math.round((padding + 0.5f) * GraphSettings.NODE_WIDTH) - r;
      double newY = GraphSettings.CELL_HEIGH * 0.5 - r;
      RoundRectangle2D rectangle2D = new RoundRectangle2D.Double(newX, newY,
                                                                 GraphSettings.CELL_HEIGH * 0.5, GraphSettings.CELL_HEIGH * 0.5, 50, 50);
      g2.fill(rectangle2D);
    }
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    ArrayList<DrawElement> rowElements = myGraphManager.getDrawElementsForRow(myRow);
    int i = 0;
    for (DrawElement element: rowElements) {
      drawDrawElement(g, i, element);
      ++i;
    }
  }
}

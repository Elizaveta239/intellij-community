
package com.jetbrains.python.debugger.threading.tool.graph.ui;


import com.intellij.ui.ColoredTableCellRenderer;
import com.jetbrains.python.debugger.threading.PyThreadingLogManager;
import com.jetbrains.python.debugger.threading.PyThreadingLogManagerImpl;
import com.jetbrains.python.debugger.threading.tool.graph.GraphManager;
import com.jetbrains.python.debugger.threading.tool.graph.ui.elements.DrawElement;

import javax.swing.*;
import java.awt.*;
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

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    ArrayList<DrawElement> rowElements = myGraphManager.getDrawElementsForRow(myRow);
    int i = 0;
    for (DrawElement element: rowElements) {
      element.drawElement(g, i);
      ++i;
    }
  }
}

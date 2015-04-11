
package com.jetbrains.python.debugger.concurrency.tool.threading.graph.ui;


import com.intellij.ui.ColoredTableCellRenderer;
import com.jetbrains.python.debugger.concurrency.PyConcurrencyLogManager;
import com.jetbrains.python.debugger.concurrency.tool.threading.PyThreadingLogManagerImpl;
import com.jetbrains.python.debugger.concurrency.tool.threading.graph.GraphManager;
import com.jetbrains.python.debugger.concurrency.tool.threading.graph.ui.elements.DrawElement;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GraphCellRenderer extends ColoredTableCellRenderer {
  private final PyThreadingLogManagerImpl myLogManager;
  private final GraphManager myGraphManager;
  private int myRow;

  public GraphCellRenderer(PyConcurrencyLogManager logManager, GraphManager graphManager) {
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

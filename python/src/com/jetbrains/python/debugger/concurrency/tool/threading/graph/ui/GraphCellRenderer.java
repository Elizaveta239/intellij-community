
package com.jetbrains.python.debugger.concurrency.tool.threading.graph.ui;


import com.intellij.ui.ColoredTableCellRenderer;
import com.jetbrains.python.debugger.concurrency.PyConcurrencyLogManager;
import com.jetbrains.python.debugger.concurrency.tool.GraphSettings;
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

  private void drawRelation(Graphics g, int parent, int child) {
    Graphics2D g2 = (Graphics2D)g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    int x_start = Math.round((parent + 0.5f) * GraphSettings.NODE_WIDTH);
    int x_finish = Math.round((child + 0.5f) * GraphSettings.NODE_WIDTH);
    int y = Math.round(GraphSettings.CELL_HEIGH * 0.5f);
    g2.drawLine(x_start, y, x_finish, y);
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
    int[] relation = myGraphManager.getRelationForRow(myRow);
    if (relation[0] != relation[1]) {
      DrawElement element = rowElements.get(relation[1]);
      ThreadState state = element.getAfter() instanceof StoppedThreadState? element.getBefore(): element.getAfter();
      state.prepareStroke(g);
      drawRelation(g, relation[0], relation[1]);
    }
  }
}

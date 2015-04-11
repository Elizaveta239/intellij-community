
package com.jetbrains.python.debugger.concurrency.tool.threading.tables;

import com.jetbrains.python.debugger.concurrency.PyConcurrencyLogManager;
import com.jetbrains.python.debugger.concurrency.tool.threading.PyThreadingLogManagerImpl;
import com.jetbrains.python.debugger.concurrency.tool.threading.ThreadingColorManager;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class ThreadCellRenderer extends DefaultTableCellRenderer {
  private final ThreadingColorManager myColorManager;
  private final PyThreadingLogManagerImpl myLogManager;

  public ThreadCellRenderer(ThreadingColorManager colorManager, PyConcurrencyLogManager logManager) {
    super();
    myColorManager = colorManager;
    myLogManager = (PyThreadingLogManagerImpl)logManager;
    setBorder(null);
  }

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    c.setBackground(myColorManager.getThreadColor(myLogManager.getThreadIdForEventAt(row)));
    return c;
  }
}

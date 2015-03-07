
package com.jetbrains.python.debugger.threading.tool.ui.tables;

import com.intellij.ui.ColoredTableCellRenderer;
import com.jetbrains.python.debugger.threading.PyThreadingLogManager;
import com.jetbrains.python.debugger.threading.PyThreadingLogManagerImpl;
import com.jetbrains.python.debugger.threading.tool.ui.ThreadingColorManager;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class ThreadCellRenderer extends DefaultTableCellRenderer {
  private final ThreadingColorManager myColorManager;
  private final PyThreadingLogManagerImpl myLogManager;

  public ThreadCellRenderer(ThreadingColorManager colorManager, PyThreadingLogManager logManager) {
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

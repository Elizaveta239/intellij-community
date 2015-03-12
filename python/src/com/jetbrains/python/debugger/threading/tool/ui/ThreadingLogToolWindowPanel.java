
package com.jetbrains.python.debugger.threading.tool.ui;

import com.intellij.openapi.project.Project;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.UIUtil;
import com.jetbrains.python.debugger.threading.PyThreadingLogManagerImpl;
import com.jetbrains.python.debugger.threading.tool.ui.tables.ThreadingTable;
import com.jetbrains.python.debugger.threading.tool.ui.tables.ThreadingTableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class ThreadingLogToolWindowPanel extends ThreadingPanel {
  private final JTable myTable;

  public ThreadingLogToolWindowPanel(Project project) {
    super(false, project);

    logManager.registerListener(new PyThreadingLogManagerImpl.Listener() {
      @Override
      public void logChanged() {
        UIUtil.invokeLaterIfNeeded(new Runnable() {
          @Override
          public void run() {
            buildLog();
          }
        });
      }
    });

    myTable = new ThreadingTable(logManager, project);
    myTable.setModel(new ThreadingTableModel(logManager));

    add(ScrollPaneFactory.createScrollPane(myTable));
  }

  public void buildLog() {
    myTable.setModel(new ThreadingTableModel(logManager));
  }

  @Override
  public void dispose() {

  }
}

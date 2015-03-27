
package com.jetbrains.python.debugger.threading.tool.ui;

import com.intellij.openapi.project.Project;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.util.ui.UIUtil;
import com.jetbrains.python.debugger.PyThreadingEvent;
import com.jetbrains.python.debugger.threading.PyThreadingLogManagerImpl;
import com.jetbrains.python.debugger.threading.tool.ui.tables.ThreadingTable;
import com.jetbrains.python.debugger.threading.tool.ui.tables.ThreadingTableModel;

import javax.swing.*;
import java.awt.*;

public class ThreadingLogToolWindowPanel extends ThreadingPanel {
  private final Project myProject;
  private final JTable myTable;
  private final JScrollPane myPane;

  public ThreadingLogToolWindowPanel(Project project) {
    super(false, project);
    myProject = project;

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

    myTable = new ThreadingTable(logManager, project, this);
    myTable.setModel(new ThreadingTableModel(logManager));
    myPane = ScrollPaneFactory.createScrollPane(myTable);

    add(myPane);
  }

  public void showStackTrace(PyThreadingEvent event) {
    LockToolWindowPanel lockPanel = new LockToolWindowPanel(myProject);
    lockPanel.buildStackTrace(event.getFrames());
    splitWindow(lockPanel);
  }

  public void splitWindow(JComponent component) {
    removeAll();
    JSplitPane p = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    p.add(myPane, JSplitPane.LEFT);
    p.add(component, JSplitPane.RIGHT);
    p.setDividerLocation((int)getSize().getWidth() * 2 / 3);
    add(p, BorderLayout.CENTER);

    validate();
    repaint();
  }

  public void buildLog() {
    myTable.setModel(new ThreadingTableModel(logManager));
  }

  @Override
  public void dispose() {

  }
}

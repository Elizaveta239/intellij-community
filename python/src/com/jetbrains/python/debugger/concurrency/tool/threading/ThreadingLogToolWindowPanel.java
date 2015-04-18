
package com.jetbrains.python.debugger.concurrency.tool.threading;

import com.intellij.openapi.project.Project;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.util.ui.UIUtil;
import com.jetbrains.python.debugger.concurrency.tool.ConcurrencyPanel;
import com.jetbrains.python.debugger.concurrency.tool.threading.tables.ThreadingTable;
import com.jetbrains.python.debugger.concurrency.tool.threading.tables.ThreadingTableModel;

import javax.swing.*;

public class ThreadingLogToolWindowPanel extends ConcurrencyPanel {
  private final Project myProject;
  private JTable myTable;

  public ThreadingLogToolWindowPanel(Project project) {
    super(false, project);
    myProject = project;
    logManager = PyThreadingLogManagerImpl.getInstance(project);

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

    initMessage();
    buildLog();
  }

  @Override
  public void initMessage() {
    removeAll();
    myLabel = new JLabel();
    myLabel.setHorizontalAlignment(JLabel.CENTER);
    myLabel.setVerticalAlignment(JLabel.CENTER);
    myLabel.setText("<html>The Threading log is empty. <br>" +
                    "Check the box \"Build diagram for concurrent programs\" " +
                    "in Settings | Build, Execution, Deployment | Python debugger</html>");
    add(myLabel);
  }

  public void buildLog() {
    if (logManager.getSize() == 0) {
      myTable = null;
      initMessage();
      return;
    }

    if (myTable == null) {
      myLabel.setVisible(false);
      myTable = new ThreadingTable((PyThreadingLogManagerImpl)logManager, myProject, this);
      myTable.setModel(new ThreadingTableModel((PyThreadingLogManagerImpl)logManager));
      myPane = ScrollPaneFactory.createScrollPane(myTable);
      add(myPane);
    }
    myTable.setModel(new ThreadingTableModel((PyThreadingLogManagerImpl)logManager));
  }

  @Override
  public void dispose() {

  }
}

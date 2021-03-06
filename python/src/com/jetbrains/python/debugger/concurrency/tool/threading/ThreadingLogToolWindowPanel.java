
package com.jetbrains.python.debugger.concurrency.tool.threading;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.util.ui.UIUtil;
import com.jetbrains.python.debugger.concurrency.PyConcurrencyLogManager;
import com.jetbrains.python.debugger.concurrency.tool.ConcurrencyPanel;
import com.jetbrains.python.debugger.concurrency.tool.ConcurrencyStatisticsTable;
import com.jetbrains.python.debugger.concurrency.tool.threading.table.ThreadingTable;
import com.jetbrains.python.debugger.concurrency.tool.threading.table.ThreadingTableModel;

import javax.swing.*;
import java.awt.*;

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
  protected JPanel createToolbarPanel() {
    final DefaultActionGroup group = new DefaultActionGroup();
    group.add(new StatisticsAction());

    final ActionToolbar actionToolBar = ActionManager.getInstance().createActionToolbar("Toolbar", group, false);
    final JPanel buttonsPanel = new JPanel(new BorderLayout());
    buttonsPanel.add(actionToolBar.getComponent(), BorderLayout.CENTER);
    return buttonsPanel;
  }

  private class StatisticsAction extends AnAction implements DumbAware {
    public StatisticsAction() {
      super("Statistical info", "Show threading statistics", AllIcons.RunConfigurations.ShowStatistics);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
      final PyConcurrencyLogManager logManager = PyThreadingLogManagerImpl.getInstance(myProject);
      UIUtil.invokeLaterIfNeeded(new Runnable() {
        @Override
        public void run() {
          ConcurrencyStatisticsTable frame = new ConcurrencyStatisticsTable(logManager);
          frame.pack();
          frame.setLocationRelativeTo(null);
          frame.setVisible(true);
        }
      });
    }
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
      setToolbar(createToolbarPanel());
    }
    myTable.setModel(new ThreadingTableModel((PyThreadingLogManagerImpl)logManager));
  }

  @Override
  public void dispose() {

  }
}

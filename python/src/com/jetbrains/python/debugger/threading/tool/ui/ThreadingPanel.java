
package com.jetbrains.python.debugger.threading.tool.ui;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.jetbrains.python.debugger.threading.PyThreadingLogManager;
import com.jetbrains.python.debugger.threading.PyThreadingLogManagerImpl;

import javax.swing.*;

public abstract class ThreadingPanel extends SimpleToolWindowPanel implements Disposable {
  private final Project myProject;
  protected final PyThreadingLogManagerImpl logManager;
  protected JLabel myLabel;

  public ThreadingPanel(boolean vertical, Project project) {
    super(vertical);
    myProject = project;
    logManager = (PyThreadingLogManagerImpl)PyThreadingLogManager.getInstance(project);

    myLabel = new JLabel();
    myLabel.setHorizontalAlignment(JLabel.CENTER);
    myLabel.setVerticalAlignment(JLabel.CENTER);
    myLabel.setText("<html>The Concurrent Activities log is empty. <br>" +
                    "Check the box \"Build diagram for concurrent programs\" " +
                    "in Settings | Build, Execution, Deployment | Python debugger</html>");
    add(myLabel);
  }
}


package com.jetbrains.python.debugger.threading.tool.ui;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.jetbrains.python.debugger.threading.PyThreadingLogManager;
import com.jetbrains.python.debugger.threading.PyThreadingLogManagerImpl;

public abstract class ThreadingPanel extends SimpleToolWindowPanel implements Disposable {
  private final Project myProject;
  protected final PyThreadingLogManagerImpl logManager;

  public ThreadingPanel(boolean vertical, Project project) {
    super(vertical);
    myProject = project;
    logManager = (PyThreadingLogManagerImpl)PyThreadingLogManager.getInstance(project);
  }
}

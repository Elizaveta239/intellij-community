
package com.jetbrains.python.debugger.concurrency.tool.threading;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.jetbrains.python.debugger.concurrency.PyConcurrencyLogManager;

import javax.swing.*;

public abstract class ThreadingPanel extends SimpleToolWindowPanel implements Disposable {
  private final Project myProject;
  protected PyConcurrencyLogManager logManager;
  protected JLabel myLabel;

  public ThreadingPanel(boolean vertical, Project project) {
    super(vertical);
    myProject = project;

    initMessage();
  }

  public abstract void initMessage();
}

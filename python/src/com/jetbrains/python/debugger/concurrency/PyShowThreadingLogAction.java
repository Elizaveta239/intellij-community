
package com.jetbrains.python.debugger.concurrency;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.jetbrains.python.debugger.concurrency.tool.threading.PyThreadingLogManagerImpl;

public class PyShowThreadingLogAction extends AnAction {
  public void actionPerformed(AnActionEvent event) {
    Project project = event.getData(PlatformDataKeys.PROJECT);

    PyConcurrencyLogManager logManager = PyThreadingLogManagerImpl.getInstance(project);
    String logString = logManager.getStringRepresentation();

    Messages.showMessageDialog(project, logString, "Threading log", Messages.getInformationIcon());

  }
}
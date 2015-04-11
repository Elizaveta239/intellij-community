
package com.jetbrains.python.debugger.concurrency;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

public class PyShowThreadingLogAction extends AnAction {
  public void actionPerformed(AnActionEvent event) {
    Project project = event.getData(PlatformDataKeys.PROJECT);

    PyConcurrencyLogManager logManager = PyConcurrencyLogManager.getInstance(project);
    String logString = logManager.getStringRepresentation();

    Messages.showMessageDialog(project, logString, "Threading log", Messages.getInformationIcon());

  }
}
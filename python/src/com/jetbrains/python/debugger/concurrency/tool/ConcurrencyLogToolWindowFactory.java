
package com.jetbrains.python.debugger.concurrency.tool;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import org.jetbrains.annotations.NotNull;

public class ConcurrencyLogToolWindowFactory implements ToolWindowFactory {
  @Override
  public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
    ConcurrencyView threadingView = new ConcurrencyView(project);
    threadingView.initToolWindow(toolWindow);
  }
}

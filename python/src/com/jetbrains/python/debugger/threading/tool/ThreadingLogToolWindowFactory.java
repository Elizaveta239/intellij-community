
package com.jetbrains.python.debugger.threading.tool;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import com.jetbrains.python.debugger.threading.tool.ui.ThreadingLogToolWindowPanel;
import org.jetbrains.annotations.NotNull;

public class ThreadingLogToolWindowFactory implements ToolWindowFactory {
  @Override
  public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
    ThreadingLogToolWindowPanel panel = new ThreadingLogToolWindowPanel(project);
    final ContentManager contentManager = toolWindow.getContentManager();
    final Content content = contentManager.getFactory().createContent(panel, null, false);
    contentManager.addContent(content);
    Disposer.register(project, panel);
  }
}

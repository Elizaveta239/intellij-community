
package com.jetbrains.python.debugger.concurrency.tool;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.jetbrains.python.debugger.concurrency.tool.asyncio.AsyncioLogToolWindowPanel;
import com.jetbrains.python.debugger.concurrency.tool.threading.ThreadingLogToolWindowPanel;
import com.jetbrains.python.debugger.concurrency.tool.threading.ThreadingPanel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConcurrencyView implements PersistentStateComponent<ConcurrencyView.State>, Disposable  {
  private final Project myProject;
  private ContentManager myContentManager;
  private ThreadingPanel myGraphPanel;
  private ThreadingPanel myAsyncioPanel;

  public ConcurrencyView(Project project) {
    myProject = project;
  }

  @Override
  public void dispose() {

  }

  @Nullable
  @Override
  public State getState() {
    return null;
  }

  @Override
  public void loadState(State state) {

  }

  static class State {

  }

  public void initToolWindow(@NotNull ToolWindow toolWindow) {
    ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
    myGraphPanel = new ThreadingLogToolWindowPanel(myProject);
    Content mainContent = contentFactory.createContent(myGraphPanel, null, false);
    mainContent.setComponent(myGraphPanel);
    mainContent.setDisplayName("Threading graph");
    Disposer.register(this, mainContent);
    myContentManager = toolWindow.getContentManager();
    myContentManager.addContent(mainContent);

    myAsyncioPanel = new AsyncioLogToolWindowPanel(myProject);
    Content lockPanelContent = contentFactory.createContent(myAsyncioPanel, null, false);
    lockPanelContent.setComponent(myAsyncioPanel);
    lockPanelContent.setDisplayName("Asyncio graph");
    Disposer.register(this, lockPanelContent);
    myContentManager = toolWindow.getContentManager();
    myContentManager.addContent(lockPanelContent);
  }
}

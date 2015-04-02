
package com.jetbrains.python.debugger.threading.tool.ui;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ThreadingView implements PersistentStateComponent<ThreadingView.State>, Disposable  {
  private final Project myProject;
  private ContentManager myContentManager;
  private ThreadingPanel myGraphPanel;
  private ThreadingPanel myLockPanel;

  public ThreadingView(Project project) {
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

    //myLockPanel = new LockToolWindowPanel(myProject);
    //Content lockPanelContent = contentFactory.createContent(myLockPanel, null, false);
    //lockPanelContent.setComponent(myLockPanel);
    //lockPanelContent.setDisplayName("Lock information");
    //Disposer.register(this, lockPanelContent);
    //myContentManager = toolWindow.getContentManager();
    //myContentManager.addContent(lockPanelContent);
  }
}

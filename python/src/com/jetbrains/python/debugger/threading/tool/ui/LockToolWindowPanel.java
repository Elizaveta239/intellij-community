

package com.jetbrains.python.debugger.threading.tool.ui;

import com.intellij.openapi.project.Project;


public class LockToolWindowPanel extends ThreadingPanel {

  public LockToolWindowPanel(Project project) {
    super(false, project);
  }

  @Override
  public void dispose() {

  }
}

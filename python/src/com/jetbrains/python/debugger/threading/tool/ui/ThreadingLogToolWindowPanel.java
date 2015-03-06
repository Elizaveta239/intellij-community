
package com.jetbrains.python.debugger.threading.tool.ui;

import com.intellij.openapi.project.Project;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.util.ui.UIUtil;
import com.jetbrains.python.debugger.threading.PyThreadingLogManagerImpl;

import javax.swing.*;

public class ThreadingLogToolWindowPanel extends ThreadingPanel {
  private final JLabel myText;

  public ThreadingLogToolWindowPanel(Project project) {
    super(false, project);
    myText = new JLabel();
    myText.setText(logManager.getStringRepresentation());

    logManager.registerListener(new PyThreadingLogManagerImpl.Listener() {
      @Override
      public void logChanged() {
        UIUtil.invokeLaterIfNeeded(new Runnable() {
          @Override
          public void run() {
            buildLog();
          }
        });
      }
    });

    add(ScrollPaneFactory.createScrollPane(myText));
  }

  public void buildLog() {
    myText.setText(logManager.getStringRepresentation());
  }

  @Override
  public void dispose() {

  }
}

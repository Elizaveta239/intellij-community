
package com.jetbrains.python.debugger.threading.tool.ui;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.util.ui.UIUtil;
import com.jetbrains.python.debugger.threading.PyThreadingLogManager;
import com.jetbrains.python.debugger.threading.PyThreadingLogManagerImpl;

import javax.swing.*;

public class ThreadingLogToolWindowPanel extends SimpleToolWindowPanel implements Disposable {
  private final Project myProject;
  private final PyThreadingLogManagerImpl logManager;
  private final JLabel myText;


  public ThreadingLogToolWindowPanel(Project project) {
    super(false);
    myProject = project;
    logManager = (PyThreadingLogManagerImpl)PyThreadingLogManager.getInstance(project);
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

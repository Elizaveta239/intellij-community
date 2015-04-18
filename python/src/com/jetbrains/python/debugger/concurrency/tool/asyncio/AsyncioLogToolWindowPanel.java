
/*
 * Copyright 2000-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jetbrains.python.debugger.concurrency.tool.asyncio;

import com.intellij.openapi.project.Project;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.util.ui.UIUtil;
import com.jetbrains.python.debugger.PyLogEvent;
import com.jetbrains.python.debugger.PyThreadingEvent;
import com.jetbrains.python.debugger.concurrency.tool.StackTracePanel;
import com.jetbrains.python.debugger.concurrency.tool.asyncio.table.AsyncioTable;
import com.jetbrains.python.debugger.concurrency.tool.asyncio.table.AsyncioTableModel;
import com.jetbrains.python.debugger.concurrency.tool.threading.PyThreadingLogManagerImpl;
import com.jetbrains.python.debugger.concurrency.tool.ConcurrencyPanel;

import javax.swing.*;
import java.awt.*;

public class AsyncioLogToolWindowPanel extends ConcurrencyPanel {
  private final Project myProject;
  private JTable myTable;
  private JScrollPane myPane;

  public AsyncioLogToolWindowPanel(Project project) {
    super(false, project);
    myProject = project;
    logManager = PyAsyncioLogManagerImpl.getInstance(project);

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

    initMessage();
    buildLog();
  }

  @Override
  public void initMessage() {
    removeAll();
    myLabel = new JLabel();
    myLabel.setHorizontalAlignment(JLabel.CENTER);
    myLabel.setVerticalAlignment(JLabel.CENTER);
    myLabel.setText("<html>The Asyncio log is empty. <br>" +
                    "Check the box \"Build diagram for concurrent programs\" " +
                    "in Settings | Build, Execution, Deployment | Python debugger</html>");
    add(myLabel);
  }


  public void splitWindow(JComponent component) {
    removeAll();
    JSplitPane p = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    p.add(myPane, JSplitPane.LEFT);
    p.add(component, JSplitPane.RIGHT);
    p.setDividerLocation((int)getSize().getWidth() * 2 / 3);
    add(p, BorderLayout.CENTER);

    validate();
    repaint();
  }

  public void buildLog() {
    if (logManager.getSize() == 0) {
      myTable = null;
      initMessage();
      return;
    }

    if (myTable == null) {
      myLabel.setVisible(false);

      myTable = new AsyncioTable((PyAsyncioLogManagerImpl)logManager, myProject, this);
      myTable.setModel(new AsyncioTableModel((PyAsyncioLogManagerImpl)logManager));
      myPane = ScrollPaneFactory.createScrollPane(myTable);
      add(myPane);
    }
    myTable.setModel(new AsyncioTableModel((PyAsyncioLogManagerImpl)logManager));
  }

  @Override
  public void dispose() {

  }
}

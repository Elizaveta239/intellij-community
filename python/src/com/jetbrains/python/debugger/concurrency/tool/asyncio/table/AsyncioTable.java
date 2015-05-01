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
package com.jetbrains.python.debugger.concurrency.tool.asyncio.table;

import com.intellij.openapi.project.Project;
import com.intellij.ui.table.JBTable;
import com.jetbrains.python.debugger.concurrency.tool.ConcurrencyColorManager;
import com.jetbrains.python.debugger.concurrency.tool.ConcurrencyPanel;
import com.jetbrains.python.debugger.concurrency.tool.ConcurrencyTable;
import com.jetbrains.python.debugger.concurrency.tool.asyncio.PyAsyncioLogManagerImpl;
import com.jetbrains.python.debugger.concurrency.tool.graph.GraphCell;
import com.jetbrains.python.debugger.concurrency.tool.graph.GraphCellRenderer;
import com.jetbrains.python.debugger.concurrency.tool.graph.GraphManager;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AsyncioTable extends ConcurrencyTable {
  protected GraphManager myGraphManager;
  protected ConcurrencyColorManager myColorManager;

  public AsyncioTable(PyAsyncioLogManagerImpl logManager, Project project, ConcurrencyPanel panel) {
    super(logManager, project, panel);
    myColorManager = new ConcurrencyColorManager();
    //myGraphManager = new AsyncioGraphManager(logManager, myColorManager);
    myGraphManager = new GraphManager(logManager, myColorManager);

    setDefaultRenderer(TaskCell.class, new TaskCellRenderer(myColorManager, myLogManager));
    setDefaultRenderer(GraphCell.class, new GraphCellRenderer(myLogManager, myGraphManager));

    addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 1) {
          JBTable target = (JBTable)e.getSource();
          int row = target.getSelectedRow();
          if (row != -1) {
            navigateToSource(myLogManager.getEventAt(row).getSourcePosition());
            myPanel.showStackTrace(myLogManager.getEventAt(row));
          }
        }
      }
    });
  }
}

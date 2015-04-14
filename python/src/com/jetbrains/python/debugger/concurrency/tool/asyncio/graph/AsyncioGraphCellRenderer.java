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
package com.jetbrains.python.debugger.concurrency.tool.asyncio.graph;

import com.intellij.ui.ColoredTableCellRenderer;
import com.jetbrains.python.debugger.concurrency.PyConcurrencyLogManager;
import com.jetbrains.python.debugger.concurrency.tool.asyncio.PyAsyncioLogManagerImpl;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class AsyncioGraphCellRenderer extends ColoredTableCellRenderer {
  private final PyAsyncioLogManagerImpl myLogManager;
  private final AsyncioGraphManager myGraphManager;
  private int myRow;

  public AsyncioGraphCellRenderer(PyConcurrencyLogManager logManager, AsyncioGraphManager graphManager) {
    myLogManager = (PyAsyncioLogManagerImpl)logManager;
    myGraphManager = graphManager;
  }

  @Override
  protected void customizeCellRenderer(JTable table, Object value, boolean selected, boolean hasFocus, int row, int column) {
    myRow = row;
    setBorder(null);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    ArrayList<DrawElement> rowElements = myGraphManager.getDrawElementsForRow(myRow);
    int i = 0;
    for (DrawElement element: rowElements) {
      element.drawElement(g, i);
      ++i;
    }
  }
}


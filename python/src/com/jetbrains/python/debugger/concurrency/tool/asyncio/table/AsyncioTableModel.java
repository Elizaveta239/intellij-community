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

import com.jetbrains.python.debugger.PyAsyncioEvent;
import com.jetbrains.python.debugger.concurrency.tool.ConcurrencyTableModel;
import com.jetbrains.python.debugger.concurrency.tool.asyncio.PyAsyncioLogManagerImpl;

public class AsyncioTableModel extends ConcurrencyTableModel {

  public AsyncioTableModel(PyAsyncioLogManagerImpl logManager) {
    super(logManager);
    COLUMN_NAMES = new String[]{"Task", "Graph", "Event"};
  }

  @Override
  public Class<?> getColumnClass(int columnIndex) {
    switch (columnIndex) {
      case TASK_COLUMN:
        return TaskCell.class;
      case GRAPH_COLUMN:
        return String.class;
      case EVENT_COLUMN:
        return String.class;
      default:
        return null;
    }
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    PyAsyncioEvent event = (PyAsyncioEvent)myLogManager.getEventAt(rowIndex);
    switch (columnIndex) {
      case TASK_COLUMN:
        return new TaskCell(event.getTaskId());
      case GRAPH_COLUMN:
        return "";
      case EVENT_COLUMN:
        //return myThreadingNamesManager.getFullEventName(event);
        return event.getCoroName();
      default:
        return null;
    }
  }
}

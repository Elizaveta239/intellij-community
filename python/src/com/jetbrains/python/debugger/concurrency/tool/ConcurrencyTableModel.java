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
package com.jetbrains.python.debugger.concurrency.tool;

import com.jetbrains.python.debugger.PyConcurrencyEvent;
import com.jetbrains.python.debugger.concurrency.PyConcurrencyLogManager;

import javax.swing.table.AbstractTableModel;

public abstract class ConcurrencyTableModel extends AbstractTableModel {
  public static final int GRAPH_COLUMN = 0;
  public static final int EVENT_COLUMN = 1;

  public static final int COLUMN_COUNT = EVENT_COLUMN + 1;
  protected static String[] COLUMN_NAMES;
  protected final PyConcurrencyLogManager<PyConcurrencyEvent> myLogManager;
  protected ConcurrencyNamesManager myThreadingNamesManager;

  public ConcurrencyTableModel(PyConcurrencyLogManager logManager) {
    myLogManager = logManager;
  }

  @Override
  public String getColumnName(int column) {
    return COLUMN_NAMES[column];
  }

  @Override
  public int getRowCount() {
    return myLogManager.getSize();
  }

  @Override
  public int getColumnCount() {
    return COLUMN_COUNT;
  }

}

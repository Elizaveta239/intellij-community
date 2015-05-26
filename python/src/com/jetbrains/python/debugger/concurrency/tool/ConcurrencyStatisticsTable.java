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

import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import com.jetbrains.python.debugger.concurrency.PyConcurrencyLogManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.HashMap;


public class ConcurrencyStatisticsTable extends JFrame {
  private String[] columnNames = {"Thread", "Work time, s", "Waiting time, s", "Lock number"};
  private HashMap<String, ConcurrencyStat> myThreadStatistics;
  private Object[] threadIds;
  private static double toSeconds = 10000;

  public ConcurrencyStatisticsTable(PyConcurrencyLogManager logManager) {
    myThreadStatistics = logManager.getStatistics();
    threadIds = myThreadStatistics.keySet().toArray();

    DefaultTableModel model = new DefaultTableModel() {
      public static final int THREAD_COLUMN = 0;
      public static final int WORK_TIME = 1;
      public static final int WAITING_TIME = 2;
      public static final int LOCK_NUMBER = 3;

      @Override
      public int getRowCount() {
        return threadIds.length;
      }

      @Override
      public int getColumnCount() {
        return LOCK_NUMBER + 1;
      }

      @Override
      public String getColumnName(int column) {
        return columnNames[column];
      }

      @Override
      public Object getValueAt(int row, int column) {
        switch (column) {
          case THREAD_COLUMN:
            return threadIds[row];
          case WORK_TIME:
            return String.format("%.2f", myThreadStatistics.get(threadIds[row]).getWorkTime() / toSeconds);
          case WAITING_TIME:
            return String.format("%.2f", myThreadStatistics.get(threadIds[row]).myWaitTime / toSeconds);
          case LOCK_NUMBER:
            return myThreadStatistics.get(threadIds[row]).myLockCount;
          default:
            return null;
        }
      }
    };

    JBTable table = new JBTable(model);
    JBScrollPane scrollPane = new JBScrollPane(table);
    add(scrollPane);
    setTitle("Statistics information");
  }

}

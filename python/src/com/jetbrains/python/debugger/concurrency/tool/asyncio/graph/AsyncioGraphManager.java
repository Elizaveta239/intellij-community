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

import com.intellij.util.containers.hash.HashMap;
import com.jetbrains.python.debugger.PyAsyncioEvent;
import com.jetbrains.python.debugger.concurrency.PyConcurrencyLogManager;
import com.jetbrains.python.debugger.concurrency.tool.ConcurrencyColorManager;
import com.jetbrains.python.debugger.concurrency.tool.asyncio.PyAsyncioLogManagerImpl;
import com.jetbrains.python.debugger.concurrency.tool.threading.PyThreadingLogManagerImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AsyncioGraphManager {
  private final PyAsyncioLogManagerImpl myLogManager;
  private final ConcurrencyColorManager myColorManager;
  private Map<String, Integer> eventIndexToId;
  private final Object myUpdateObject = new Object();
  private DrawElement[][] myGraphScheme;
  private int[] myEventsCountForRow;

  public AsyncioGraphManager(PyConcurrencyLogManager logManager, ConcurrencyColorManager colorManager) {
    myLogManager = (PyAsyncioLogManagerImpl)logManager;
    myColorManager = colorManager;
    updateGraph();

    myLogManager.registerListener(new PyConcurrencyLogManager.Listener() {
      @Override
      public void logChanged() {
        updateGraph();
      }
    });
  }

  public ArrayList<DrawElement> getDrawElementsForRow(int row) {
    ArrayList<DrawElement> rowElements = new ArrayList<DrawElement>();
    synchronized (myUpdateObject) {
      for (int i = 0; i < myEventsCountForRow[row]; ++i) {
        rowElements.add(myGraphScheme[row][i]);
      }
    }
    return rowElements;
  }

  private void updateGraph() {
    synchronized (myUpdateObject) {
      myGraphScheme = new DrawElement[myLogManager.getSize() + 1][];
      myEventsCountForRow = new int[myLogManager.getSize() + 1];
      eventIndexToId = new HashMap<String, Integer>();
      List<PyAsyncioEvent> myLog = myLogManager.getLog();
      int currentMaxId = 0;
      int i = 0;
      for (PyAsyncioEvent event: myLog) {
        String coroId = event.getEventId();
        int coroNum;
        if (eventIndexToId.containsKey(coroId)) {
          coroNum = eventIndexToId.get(coroId);
        } else {
          eventIndexToId.put(coroId, currentMaxId);
          coroNum = currentMaxId;
          currentMaxId++;
        }
        myEventsCountForRow[i] = currentMaxId;
        myGraphScheme[i] = new DrawElement[currentMaxId];
        if (i != 0) {
          for (int j = 0; j < myGraphScheme[i - 1].length; ++j) {
            myGraphScheme[i][j] = myGraphScheme[i - 1][j].getNextElement();
          }
        }
        myGraphScheme[i][coroNum] = new CoroElement(myColorManager.getItemColor(event.getEventId()));
        ++i;
      }
    }
  }
}

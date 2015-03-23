
package com.jetbrains.python.debugger.threading.tool.graph;

import com.intellij.util.containers.hash.HashMap;
import com.intellij.util.ui.UIUtil;
import com.jetbrains.python.debugger.PyThreadingEvent;
import com.jetbrains.python.debugger.threading.PyThreadingLogManager;
import com.jetbrains.python.debugger.threading.PyThreadingLogManagerImpl;
import com.jetbrains.python.debugger.threading.tool.graph.ui.DrawElement;
import com.jetbrains.python.debugger.threading.tool.graph.ui.elements.*;
import com.jetbrains.python.debugger.threading.tool.ui.ThreadingColorManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GraphManager {
  private final PyThreadingLogManagerImpl myLogManager;
  private final ThreadingColorManager myColorManager;
  private DrawElement[][] myGraphScheme;
  private int[] threadCountForRow;
  private Map<String, Integer> threadIndexToId;
  private int currentMaxThread = 0;

  public GraphManager(PyThreadingLogManager logManager, ThreadingColorManager colorManager) {
    myLogManager = (PyThreadingLogManagerImpl)logManager;
    myColorManager = colorManager;
    threadIndexToId = new HashMap<String, Integer>();
    updateGraph();

    myLogManager.registerListener(new PyThreadingLogManagerImpl.Listener() {
      @Override
      public void logChanged() {
        updateGraph();
      }
    });
  }

  public String getStringForRow(int row) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < threadCountForRow[row]; ++i) {
      sb.append(myGraphScheme[row][i].toString()).append(" ");
    }
    return sb.toString();
  }

  public ArrayList<DrawElement> getDrawElementsForRow(int row) {
    ArrayList<DrawElement> rowElements = new ArrayList<DrawElement>();
    for (int i = 0; i < threadCountForRow[row]; ++i) {
      rowElements.add(myGraphScheme[row][i]);
    }
    return rowElements;
  }

  private static DrawElement getDrawElementForEvent(PyThreadingEvent event) {
    switch (event.getType()) {
      case START:
        return new ThreadStart();
      case JOIN:
        return new ThreadFinish();
      case STOP:
        return new ThreadFinish();
      case ACQUIRE_BEGIN:
        return new AcquireBegin();
      case ACQUIRE_END:
        return new AcquireEnd();
      case RELEASE:
        return new Release();
      default:
        return new Empty();
    }
  }

  public void updateGraph() {
    myGraphScheme = new DrawElement[myLogManager.getSize() + 1][];
    threadCountForRow = new int[myLogManager.getSize() + 1];
    List<PyThreadingEvent> myLog = myLogManager.getLog();
    currentMaxThread = 0;
    int i = 0;
    for (PyThreadingEvent event: myLog) {
      String eventThreadId = event.getThreadId();

      if (event.isThreadEvent() && event.getType() == PyThreadingEvent.EVENT_TYPE.START) {
        DrawElement element;
        element = new ThreadStart(myColorManager.getThreadColor(eventThreadId));
        currentMaxThread++;
        threadIndexToId.put(eventThreadId, currentMaxThread - 1);

        myGraphScheme[i] = new DrawElement[currentMaxThread];
        for (int j = 0; j < currentMaxThread - 1; ++j) {
          myGraphScheme[i][j] = myGraphScheme[i - 1][j].getNextElement();
          myGraphScheme[i][j].setColor(myGraphScheme[i - 1][j].getColor());
        }
        myGraphScheme[i][currentMaxThread - 1] = element;

      } else {
        int eventThreadIdInt = threadIndexToId.containsKey(eventThreadId) ? threadIndexToId.get(eventThreadId): 0;

        myGraphScheme[i] = new DrawElement[currentMaxThread];
        for (int j = 0; j < currentMaxThread; ++j) {
          if (j != eventThreadIdInt) {
            myGraphScheme[i][j] = myGraphScheme[i - 1][j].getNextElement();
            myGraphScheme[i][j].setColor(myGraphScheme[i - 1][j].getColor());
          }
        }
        myGraphScheme[i][eventThreadIdInt] = getDrawElementForEvent(event);
        myGraphScheme[i][eventThreadIdInt].setColor(myColorManager.getThreadColor(eventThreadId));
      }
      threadCountForRow[i] = currentMaxThread;
      ++i;
    }
  }
}

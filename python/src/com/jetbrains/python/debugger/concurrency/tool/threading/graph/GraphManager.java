
package com.jetbrains.python.debugger.concurrency.tool.threading.graph;

import com.intellij.util.containers.hash.HashMap;
import com.jetbrains.python.debugger.PyThreadingEvent;
import com.jetbrains.python.debugger.concurrency.PyConcurrencyLogManager;
import com.jetbrains.python.debugger.concurrency.tool.threading.PyThreadingLogManagerImpl;
import com.jetbrains.python.debugger.concurrency.tool.threading.graph.ui.*;
import com.jetbrains.python.debugger.concurrency.tool.threading.graph.ui.elements.*;
import com.jetbrains.python.debugger.concurrency.tool.ConcurrencyColorManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GraphManager {
  private final PyThreadingLogManagerImpl myLogManager;
  private final ConcurrencyColorManager myColorManager;
  private DrawElement[][] myGraphScheme;
  private int[] threadCountForRow;
  private Map<String, Integer> threadIndexToId;
  private final Object myUpdateObject = new Object();
  private int currentMaxThread = 0;

  public GraphManager(PyConcurrencyLogManager logManager, ConcurrencyColorManager colorManager) {
    myLogManager = (PyThreadingLogManagerImpl)logManager;
    myColorManager = colorManager;
    threadIndexToId = new HashMap<String, Integer>();
    updateGraph();

    myLogManager.registerListener(new PyConcurrencyLogManager.Listener() {
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

  private DrawElement getDrawElementForEvent(PyThreadingEvent event, DrawElement previousElement, int index) {
    switch (event.getType()) {
      case START:
        return new EventDrawElement(null, new StoppedThreadState(), new RunThreadState());
      case JOIN:
        return new EventDrawElement(null, previousElement.getAfter(), previousElement.getAfter());
      case STOP:
        return new EventDrawElement(null, previousElement.getAfter(), new StoppedThreadState());
      case ACQUIRE_BEGIN:
        return new EventDrawElement(null, previousElement.getAfter(), new LockWaitThreadState());
      case ACQUIRE_END:
        return new EventDrawElement(null, previousElement.getAfter(), new LockOwnThreadState());
      case RELEASE:
        return new EventDrawElement(null, previousElement.getAfter(), myLogManager.getThreadStateAt(index, event.getThreadId()));
      default:
        return new SimpleDrawElement(null, new StoppedThreadState(), new StoppedThreadState());
    }
  }

  public void updateGraph() {
    synchronized (myUpdateObject) {
      myGraphScheme = new DrawElement[myLogManager.getSize() + 1][];
      threadCountForRow = new int[myLogManager.getSize() + 1];
      List<PyThreadingEvent> myLog = myLogManager.getLog();
      currentMaxThread = 0;
      int i = 0;
      for (PyThreadingEvent event : myLog) {
        String eventThreadId = event.getThreadId();

        if (event.isThreadEvent() && event.getType() == PyThreadingEvent.EventType.START) {
          DrawElement element;
          element = new EventDrawElement(myColorManager.getItemColor(eventThreadId), new StoppedThreadState(), new RunThreadState());
          currentMaxThread++;
          threadIndexToId.put(eventThreadId, currentMaxThread - 1);

          myGraphScheme[i] = new DrawElement[currentMaxThread];
          for (int j = 0; j < currentMaxThread - 1; ++j) {
            myGraphScheme[i][j] = myGraphScheme[i - 1][j].getNextElement();
            myGraphScheme[i][j].setColor(myGraphScheme[i - 1][j].getColor());
          }
          myGraphScheme[i][currentMaxThread - 1] = element;

        }
        else {
          int eventThreadIdInt = threadIndexToId.containsKey(eventThreadId) ? threadIndexToId.get(eventThreadId) : 0;

          myGraphScheme[i] = new DrawElement[currentMaxThread];
          for (int j = 0; j < currentMaxThread; ++j) {
            if (j != eventThreadIdInt) {
              myGraphScheme[i][j] = myGraphScheme[i - 1][j].getNextElement();
              myGraphScheme[i][j].setColor(myGraphScheme[i - 1][j].getColor());
            }
          }
          myGraphScheme[i][eventThreadIdInt] = getDrawElementForEvent(event, myGraphScheme[i - 1][eventThreadIdInt], i);
          myGraphScheme[i][eventThreadIdInt].setColor(myColorManager.getItemColor(eventThreadId));
        }
        threadCountForRow[i] = currentMaxThread;
        ++i;
      }
    }
  }
}

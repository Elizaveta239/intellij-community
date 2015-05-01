
package com.jetbrains.python.debugger.concurrency.tool.graph;

import com.intellij.util.containers.hash.HashMap;
import com.jetbrains.python.debugger.PyConcurrencyEvent;
import com.jetbrains.python.debugger.PyThreadEvent;
import com.jetbrains.python.debugger.concurrency.PyConcurrencyLogManager;
import com.jetbrains.python.debugger.concurrency.tool.ConcurrencyColorManager;
import com.jetbrains.python.debugger.concurrency.tool.graph.elements.DrawElement;
import com.jetbrains.python.debugger.concurrency.tool.graph.elements.EventDrawElement;
import com.jetbrains.python.debugger.concurrency.tool.graph.elements.SimpleDrawElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GraphManager {
  private final PyConcurrencyLogManager myLogManager;
  private final ConcurrencyColorManager myColorManager;
  private DrawElement[][] myGraphScheme;
  private int[] threadCountForRow;
  private Map<String, Integer> threadIndexToId;
  private final Object myUpdateObject = new Object();
  private int currentMaxThread = 0;
  private int[][] relations;

  public GraphManager(PyConcurrencyLogManager logManager, ConcurrencyColorManager colorManager) {
    myLogManager = logManager;
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
    for (DrawElement element: myGraphScheme[row]) {
      rowElements.add(element);
    }
    return rowElements;
  }

  private DrawElement getDrawElementForEvent(PyConcurrencyEvent event, DrawElement previousElement, int index) {
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

  private void addRelation(int index, int parent, int child) {
    relations[index][0] = parent;
    relations[index][1] = child;
  }

  public int[] getRelationForRow(int row) {
    return relations[row];
  }

  public void updateGraph() {
    synchronized (myUpdateObject) {
      myGraphScheme = new DrawElement[myLogManager.getSize() + 1][];
      threadCountForRow = new int[myLogManager.getSize() + 1];
      List<PyConcurrencyEvent> myLog = myLogManager.getLog();
      relations = new int[myLogManager.getSize() + 1][2];
      currentMaxThread = 0;
      int i = 0;
      for (PyConcurrencyEvent event : myLog) {
        String eventThreadId = event.getThreadId();

        if (event.isThreadEvent() && event.getType() == PyConcurrencyEvent.EventType.START) {
          DrawElement element;
          element = new EventDrawElement(myColorManager.getItemColor(eventThreadId), new StoppedThreadState(), new RunThreadState());
          currentMaxThread++;
          threadIndexToId.put(eventThreadId, currentMaxThread - 1);

          String parentId = ((PyThreadEvent)event).getParentThreadId();
          if (parentId != null) {
            int parentNum = threadIndexToId.get(parentId);
            int eventNum = currentMaxThread - 1;
            addRelation(i, parentNum, eventNum);
          }

          myGraphScheme[i] = new DrawElement[currentMaxThread];
          for (int j = 0; j < currentMaxThread - 1; ++j) {
            myGraphScheme[i][j] = myGraphScheme[i - 1][j].getNextElement();
            myGraphScheme[i][j].setColor(myGraphScheme[i - 1][j].getColor());
          }
          myGraphScheme[i][currentMaxThread - 1] = element;

        }
        else {
          int eventThreadIdInt = threadIndexToId.containsKey(eventThreadId) ? threadIndexToId.get(eventThreadId) : 0;

          if ((event instanceof PyThreadEvent) && (((PyThreadEvent)event).getParentThreadId() != null)) {
            int parentNum = threadIndexToId.get(((PyThreadEvent)event).getParentThreadId());
            int eventNum = eventThreadIdInt;
            addRelation(i, parentNum, eventNum);
          }

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

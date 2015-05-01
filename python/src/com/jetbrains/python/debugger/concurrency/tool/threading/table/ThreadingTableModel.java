
package com.jetbrains.python.debugger.concurrency.tool.threading.table;

import com.jetbrains.python.debugger.PyConcurrencyEvent;
import com.jetbrains.python.debugger.concurrency.tool.ConcurrencyTableModel;
import com.jetbrains.python.debugger.concurrency.tool.graph.GraphCell;
import com.jetbrains.python.debugger.concurrency.tool.threading.PyThreadingLogManagerImpl;
import com.jetbrains.python.debugger.concurrency.tool.threading.ThreadingNamesManager;

public class ThreadingTableModel extends ConcurrencyTableModel {
  private final ThreadingNamesManager myThreadingNamesManager;

  public ThreadingTableModel(PyThreadingLogManagerImpl logManager) {
    super(logManager);
    myThreadingNamesManager = new ThreadingNamesManager();
    COLUMN_NAMES = new String[]{"Thread", "Graph", "Event"};
  }

  @Override
  public Class<?> getColumnClass(int columnIndex) {
    switch (columnIndex) {
      case TASK_COLUMN:
        return ThreadCell.class;
      case GRAPH_COLUMN:
        return GraphCell.class;
      case EVENT_COLUMN:
        return String.class;
      default:
        return null;
    }
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    PyConcurrencyEvent event = myLogManager.getEventAt(rowIndex);
    switch (columnIndex) {
      case TASK_COLUMN:
        return new ThreadCell(event.getThreadName());
      case GRAPH_COLUMN:
        return new GraphCell();
      case EVENT_COLUMN:
        return myThreadingNamesManager.getFullEventName(event);
      default:
        return null;
    }
  }
}

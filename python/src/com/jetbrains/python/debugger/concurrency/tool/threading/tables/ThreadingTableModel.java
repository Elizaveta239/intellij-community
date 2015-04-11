
package com.jetbrains.python.debugger.concurrency.tool.threading.tables;

import com.jetbrains.python.debugger.PyThreadingEvent;
import com.jetbrains.python.debugger.concurrency.tool.threading.PyThreadingLogManagerImpl;
import com.jetbrains.python.debugger.concurrency.tool.threading.graph.ui.GraphCell;
import com.jetbrains.python.debugger.concurrency.tool.threading.ThreadingNamesManager;

import javax.swing.table.AbstractTableModel;

public class ThreadingTableModel extends AbstractTableModel {
  public static final int THREAD_COLUMN = 0;
  public static final int GRAPH_COLUMN = 1;
  public static final int EVENT_COLUMN = 2;

  public static final int COLUMN_COUNT = EVENT_COLUMN + 1;

  private static final String[] COLUMN_NAMES = {"Thread", "Graph", "Event"};

  private final PyThreadingLogManagerImpl myLogManager;
  private final ThreadingNamesManager myThreadingNamesManager;

  public ThreadingTableModel(PyThreadingLogManagerImpl logManager) {
    myLogManager = logManager;
    myThreadingNamesManager = new ThreadingNamesManager();
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

  @Override
  public Class<?> getColumnClass(int columnIndex) {
    switch (columnIndex) {
      case THREAD_COLUMN:
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
    PyThreadingEvent event = myLogManager.getEventAt(rowIndex);
    switch (columnIndex) {
      case THREAD_COLUMN:
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

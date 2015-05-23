
package com.jetbrains.python.debugger.concurrency.tool.threading.table;

import com.jetbrains.python.debugger.PyConcurrencyEvent;
import com.jetbrains.python.debugger.concurrency.tool.ConcurrencyNamesManager;
import com.jetbrains.python.debugger.concurrency.tool.ConcurrencyTableModel;
import com.jetbrains.python.debugger.concurrency.tool.graph.GraphCell;
import com.jetbrains.python.debugger.concurrency.tool.threading.PyThreadingLogManagerImpl;

public class ThreadingTableModel extends ConcurrencyTableModel {

  public ThreadingTableModel(PyThreadingLogManagerImpl logManager) {
    super(logManager);
    myThreadingNamesManager = new ConcurrencyNamesManager();
    COLUMN_NAMES = new String[]{"Graph", "Event"};
  }

  @Override
  public Class<?> getColumnClass(int columnIndex) {
    switch (columnIndex) {
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
      case GRAPH_COLUMN:
        return new GraphCell();
      case EVENT_COLUMN:
        return myThreadingNamesManager.getFullEventName(event);
      default:
        return null;
    }
  }
}

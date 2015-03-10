
package com.jetbrains.python.debugger.threading.tool.ui.tables;

import com.intellij.ui.table.JBTable;
import com.jetbrains.python.debugger.threading.PyThreadingLogManagerImpl;
import com.jetbrains.python.debugger.threading.tool.graph.GraphManager;
import com.jetbrains.python.debugger.threading.tool.graph.ui.GraphCell;
import com.jetbrains.python.debugger.threading.tool.graph.ui.GraphCellRenderer;
import com.jetbrains.python.debugger.threading.tool.graph.GraphSettings;
import com.jetbrains.python.debugger.threading.tool.ui.ThreadingColorManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

public class ThreadingTable extends JBTable {
  private final ThreadingColorManager myColorManager;
  private final PyThreadingLogManagerImpl myLogManager;
  private final GraphManager myGraphManager;

  private static final int THREAD_COLUMN_WIDTH = 600;

  private boolean myColumnsInitialized = false;

  public ThreadingTable(PyThreadingLogManagerImpl logManager) {
    super();

    myLogManager = logManager;
    myColorManager = new ThreadingColorManager();
    myGraphManager = new GraphManager(myLogManager, myColorManager);
    setDefaultRenderer(GraphCell.class, new GraphCellRenderer(myLogManager, myGraphManager));
    setDefaultRenderer(ThreadCell.class, new ThreadCellRenderer(myColorManager, myLogManager));

    setRowHeight(GraphSettings.CELL_HEIGH);
    setShowHorizontalLines(false);
  }

  @Override
  public void setModel(@NotNull TableModel model) {
    super.setModel(model);
    if (!myColumnsInitialized) {
      myColumnsInitialized = true;
      setColumnSizes();
    }
  }

  protected void setColumnSizes() {
    for (int i = 0; i < getColumnCount(); ++i) {
      TableColumn column = getColumnModel().getColumn(i);
      if (i == ThreadingTableModel.THREAD_COLUMN) {
        column.setMaxWidth(THREAD_COLUMN_WIDTH);
      }
    }
  }
}

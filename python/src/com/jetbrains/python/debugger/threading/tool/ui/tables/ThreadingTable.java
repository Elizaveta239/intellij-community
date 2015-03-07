
package com.jetbrains.python.debugger.threading.tool.ui.tables;

import com.intellij.ui.table.JBTable;
import com.jetbrains.python.debugger.threading.PyThreadingLogManagerImpl;
import com.jetbrains.python.debugger.threading.tool.graph.GraphCell;
import com.jetbrains.python.debugger.threading.tool.graph.GraphCellRenderer;
import com.jetbrains.python.debugger.threading.tool.ui.ThreadingColorManager;

public class ThreadingTable extends JBTable {
  private final ThreadingColorManager myColorManager;
  private final PyThreadingLogManagerImpl myLogManager;

  public ThreadingTable(PyThreadingLogManagerImpl logManager) {
    super();

    myLogManager = logManager;
    myColorManager = new ThreadingColorManager();
    setDefaultRenderer(GraphCell.class, new GraphCellRenderer(myColorManager, myLogManager));
    setDefaultRenderer(ThreadCell.class, new ThreadCellRenderer(myColorManager, myLogManager));

    setShowHorizontalLines(false);
  }

}

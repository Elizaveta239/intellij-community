
package com.jetbrains.python.debugger.concurrency.tool.threading.table;

import com.intellij.openapi.project.Project;
import com.intellij.ui.table.JBTable;
import com.jetbrains.python.debugger.concurrency.tool.ConcurrencyColorManager;
import com.jetbrains.python.debugger.concurrency.tool.ConcurrencyPanel;
import com.jetbrains.python.debugger.concurrency.tool.ConcurrencyTable;
import com.jetbrains.python.debugger.concurrency.tool.GraphSettings;
import com.jetbrains.python.debugger.concurrency.tool.graph.GraphCell;
import com.jetbrains.python.debugger.concurrency.tool.graph.GraphCellRenderer;
import com.jetbrains.python.debugger.concurrency.tool.graph.GraphManager;
import com.jetbrains.python.debugger.concurrency.tool.threading.PyThreadingLogManagerImpl;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ThreadingTable extends ConcurrencyTable {
  private final ConcurrencyColorManager myColorManager;
  private final GraphManager myGraphManager;

  public ThreadingTable(PyThreadingLogManagerImpl logManager, Project project, ConcurrencyPanel panel) {
    super(logManager, project, panel);
    myColorManager = new ConcurrencyColorManager();
    myGraphManager = new GraphManager(myLogManager, myColorManager);
    setDefaultRenderer(GraphCell.class, new GraphCellRenderer(myLogManager, myGraphManager));
    setDefaultRenderer(ThreadCell.class, new ThreadCellRenderer(myColorManager, myLogManager));

    setRowHeight(GraphSettings.CELL_HEIGH);
    setShowHorizontalLines(false);

    addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 1) {
          JBTable target = (JBTable)e.getSource();
          int row = target.getSelectedRow();
          if (row != -1) {
            navigateToSource(myLogManager.getEventAt(row).getSourcePosition());
            myPanel.showStackTrace(myLogManager.getEventAt(row));
          }
        }
      }
    });
  }

}

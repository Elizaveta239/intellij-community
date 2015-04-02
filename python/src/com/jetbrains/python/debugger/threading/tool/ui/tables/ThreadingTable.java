
package com.jetbrains.python.debugger.threading.tool.ui.tables;

import com.intellij.openapi.project.Project;
import com.intellij.ui.AppUIUtil;
import com.intellij.ui.table.JBTable;
import com.intellij.xdebugger.XSourcePosition;
import com.jetbrains.python.debugger.threading.PyThreadingLogManagerImpl;
import com.jetbrains.python.debugger.threading.tool.graph.GraphManager;
import com.jetbrains.python.debugger.threading.tool.graph.ui.GraphCell;
import com.jetbrains.python.debugger.threading.tool.graph.ui.GraphCellRenderer;
import com.jetbrains.python.debugger.threading.tool.graph.GraphSettings;
import com.jetbrains.python.debugger.threading.tool.ui.ThreadingColorManager;
import com.jetbrains.python.debugger.threading.tool.ui.ThreadingLogToolWindowPanel;
import com.jetbrains.python.debugger.threading.tool.ui.ThreadingPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.table.TableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ThreadingTable extends JBTable {
  private final ThreadingColorManager myColorManager;
  private final PyThreadingLogManagerImpl myLogManager;
  private final GraphManager myGraphManager;
  private final Project myProject;
  private final ThreadingLogToolWindowPanel myPanel;

  private boolean myColumnsInitialized = false;

  public ThreadingTable(PyThreadingLogManagerImpl logManager, Project project, ThreadingPanel panel) {
    super();

    myLogManager = logManager;
    myProject = project;
    myPanel = (ThreadingLogToolWindowPanel)panel;
    myColorManager = new ThreadingColorManager();
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
            navigateToSource(myLogManager.getSourcePositionForEventNumber(row));
            myPanel.showStackTrace(myLogManager.getEventAt(row));
          }
        }
      }
    });

  }

  private void navigateToSource(final XSourcePosition sourcePosition) {
    if (sourcePosition != null) {
      AppUIUtil.invokeOnEdt(new Runnable() {
        @Override
        public void run() {
          sourcePosition.createNavigatable(myProject).navigate(true);
        }
      }, myProject.getDisposed());
    }
  }

  @Override
  public void setModel(@NotNull TableModel model) {
    super.setModel(model);
    if (!myColumnsInitialized) {
      myColumnsInitialized = true;
      //setColumnSizes();
    }
  }
}

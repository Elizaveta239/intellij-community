
package com.jetbrains.python.debugger.threading.tool.ui.tables;

import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.AppUIUtil;
import com.intellij.ui.table.JBTable;
import com.intellij.xdebugger.XSourcePosition;
import com.jetbrains.python.debugger.threading.PyThreadingLogManagerImpl;
import com.jetbrains.python.debugger.threading.tool.graph.GraphManager;
import com.jetbrains.python.debugger.threading.tool.graph.ui.GraphCell;
import com.jetbrains.python.debugger.threading.tool.graph.ui.GraphCellRenderer;
import com.jetbrains.python.debugger.threading.tool.graph.GraphSettings;
import com.jetbrains.python.debugger.threading.tool.ui.ThreadingColorManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ThreadingTable extends JBTable {
  private final ThreadingColorManager myColorManager;
  private final PyThreadingLogManagerImpl myLogManager;
  private final GraphManager myGraphManager;
  private final Project myProject;

  private static final int THREAD_COLUMN_WIDTH = 600;

  private boolean myColumnsInitialized = false;

  public ThreadingTable(PyThreadingLogManagerImpl logManager, Project project) {
    super();

    myLogManager = logManager;
    myProject = project;
    myColorManager = new ThreadingColorManager();
    myGraphManager = new GraphManager(myLogManager, myColorManager);
    setDefaultRenderer(GraphCell.class, new GraphCellRenderer(myLogManager, myGraphManager));
    setDefaultRenderer(ThreadCell.class, new ThreadCellRenderer(myColorManager, myLogManager));

    setRowHeight(GraphSettings.CELL_HEIGH);
    setShowHorizontalLines(false);

    addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
          JBTable target = (JBTable)e.getSource();
          int row = target.getSelectedRow();
          navigateToSource(myLogManager.getSourcePositionForEventNumber(row));
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

  protected void setColumnSizes() {
    for (int i = 0; i < getColumnCount(); ++i) {
      TableColumn column = getColumnModel().getColumn(i);
      if (i == ThreadingTableModel.THREAD_COLUMN) {
        column.setMaxWidth(THREAD_COLUMN_WIDTH);
      }
    }
  }
}

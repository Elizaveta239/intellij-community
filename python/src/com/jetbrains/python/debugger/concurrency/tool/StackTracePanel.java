

package com.jetbrains.python.debugger.concurrency.tool;

import com.intellij.icons.AllIcons;
import com.intellij.ide.util.treeView.TreeState;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.AppUIUtil;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.tree.TreeUtil;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.impl.XSourcePositionImpl;
import com.jetbrains.python.debugger.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.List;


public class StackTracePanel extends SimpleToolWindowPanel implements Disposable {
  private final Tree myTree;
  private final DefaultTreeModel myModel;
  private final Project myProject;
  private final DefaultMutableTreeNode rootNode;

  public StackTracePanel(boolean vertical, Project project) {
    super(vertical);
    myProject = project;

    String root = "root";
    rootNode = new DefaultMutableTreeNode(root);
    myModel = new DefaultTreeModel(rootNode);
    myTree = new Tree(myModel);
    myTree.setShowsRootHandles(true);
    myTree.setRootVisible(false);
    myTree.setLayout(new BorderLayout());
    TreeUtil.installActions(myTree);
    myTree.setLargeModel(true);

    myTree.setCellRenderer(new ColoredTreeCellRenderer() {
      @Override
      public void customizeCellRenderer(@NotNull JTree tree,
                                        Object value,
                                        boolean selected,
                                        boolean expanded,
                                        boolean leaf,
                                        int row,
                                        boolean hasFocus) {

        if (!(value instanceof DefaultMutableTreeNode)) {
          append(value.toString(), SimpleTextAttributes.REGULAR_ATTRIBUTES);
          return;
        }
        Object userObject = ((DefaultMutableTreeNode)value).getUserObject();
        if (userObject instanceof PyStackFrameInfo) {
          setIcon(AllIcons.Debugger.StackFrame);
          PyStackFrameInfo frameInfo = (PyStackFrameInfo)userObject;
          PySourcePosition position = frameInfo.getPosition();
          if (position == null) {
            append("<frame not available>", SimpleTextAttributes.GRAY_ATTRIBUTES);
            return;
          }

          boolean isExternal = true;
          final VirtualFile file = LocalFileSystem.getInstance().findFileByPath(position.getFile());
          String filename;
          if (file != null) {
            final Document document = FileDocumentManager.getInstance().getDocument(file);
            if (document != null) {
              isExternal = !ProjectRootManager.getInstance(myProject).getFileIndex().isInContent(file);
            }
            filename = file.getName();
          } else {
            append("<frame not available>", SimpleTextAttributes.GRAY_ATTRIBUTES);
            return;
          }

          append(frameInfo.getName(), gray(SimpleTextAttributes.REGULAR_ATTRIBUTES, isExternal));
          append(", ", gray(SimpleTextAttributes.REGULAR_ATTRIBUTES, isExternal));
          append(filename, gray(SimpleTextAttributes.REGULAR_ATTRIBUTES, isExternal));
          append(":", gray(SimpleTextAttributes.REGULAR_ATTRIBUTES, isExternal));
          append(Integer.toString(position.getLine() + 1), gray(SimpleTextAttributes.REGULAR_ATTRIBUTES, isExternal));
        }
      }
    });

    myTree.addTreeSelectionListener(new TreeSelectionListener() {
      @Override
      public void valueChanged(TreeSelectionEvent e) {
        TreePath path=e.getNewLeadSelectionPath();
        if (path != null) {
          DefaultMutableTreeNode frameNode=(DefaultMutableTreeNode)path.getLastPathComponent();
          Object userObject = frameNode.getUserObject();
          if (userObject instanceof PyStackFrameInfo) {
            PyStackFrameInfo frameInfo = (PyStackFrameInfo)userObject;
            PySourcePosition position = frameInfo.getPosition();
            VirtualFile vFile = LocalFileSystem.getInstance().findFileByPath(position.getFile());
            navigateToSource(XSourcePositionImpl.create(vFile, position.getLine() - 1));
          }
        }
      }
    });

    TreeUtil.expand(myTree, 1);
    add(ScrollPaneFactory.createScrollPane(myTree));
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

  private static SimpleTextAttributes gray(SimpleTextAttributes attributes, boolean gray) {
    if (!gray) {
      return attributes;
    }
    else {
      return (attributes.getStyle() & SimpleTextAttributes.STYLE_ITALIC) != 0
             ? SimpleTextAttributes.GRAY_ITALIC_ATTRIBUTES : SimpleTextAttributes.GRAYED_ATTRIBUTES;
    }
  }

  public void buildStackTrace(List<PyStackFrameInfo> frames) {
    final TreeState treeState = TreeState.createOn(myTree, rootNode);
    rootNode.removeAllChildren();

    for(PyStackFrameInfo frame: frames) {
      DefaultMutableTreeNode frameNode = new DefaultMutableTreeNode(frame);
      rootNode.add(frameNode);
    }

    myModel.nodeStructureChanged(rootNode);
    treeState.applyTo(myTree);
  }

  @Override
  public void dispose() {

  }
}

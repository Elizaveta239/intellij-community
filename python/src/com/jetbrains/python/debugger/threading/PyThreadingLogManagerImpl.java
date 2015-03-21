
package com.jetbrains.python.debugger.threading;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.file.impl.FileManagerImpl;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebugSessionListener;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.impl.XSourcePositionImpl;
import com.jetbrains.python.debugger.PyThreadingEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PyThreadingLogManagerImpl extends PyThreadingLogManager {
  private final Project myProject;
  private XDebugSession lastSession;
  private List<PyThreadingEvent> myLog;
  private final List<Listener> myListeners;

  public PyThreadingLogManagerImpl(Project project) {
    myProject = project;
    myLog = new ArrayList<PyThreadingEvent>();
    myListeners = new ArrayList<Listener>();
  }

  public List<PyThreadingEvent> getLog() {
    return myLog;
  }

  public Integer getSize() {
    return myLog.size();
  }

  public PyThreadingEvent getEventAt(int index) {
    return myLog.get(index);
  }

  public String getThreadIdForEventAt(int index) {
    return myLog.get(index).getThreadId();
  }

  public String getStringRepresentation() {
    StringBuilder resultBuilder = new StringBuilder();
    resultBuilder.append("<html>Size: ").append(myLog.size()).append("<br>");
    for (PyThreadingEvent event: getLog()) {
      resultBuilder.append(event.toString());
    }
    resultBuilder.append("</html>");
    return resultBuilder.toString();
  }

  public void addSessionListener() {
    lastSession.addSessionListener(new XDebugSessionListener() {
      @Override
      public void sessionPaused() {
        notifyListeners();
      }

      @Override
      public void sessionResumed() {
      }

      @Override
      public void sessionStopped() {
        notifyListeners();
      }

      @Override
      public void stackFrameChanged() {

      }

      @Override
      public void beforeSessionResume() {

      }
    });
  }

  public XSourcePosition getSourcePositionForEventNumber(int num) {
    final PyThreadingEvent event = myLog.get(num);
    VirtualFile vFile = LocalFileSystem.getInstance().findFileByPath(event.getFileName());
    return XSourcePositionImpl.create(vFile, event.getLine());
  }

  @Override
  public void recordEvent(@NotNull XDebugSession debugSession, @NotNull PyThreadingEvent event) {
    if ((lastSession == null) || (debugSession != lastSession)) {
      lastSession = debugSession;
      myLog = new ArrayList<PyThreadingEvent>();
      addSessionListener();
    }
    myLog.add(event);
    notifyListeners();
  }

  public interface Listener {
    void logChanged();
  }

  public void registerListener(@NotNull Listener listener) {
    synchronized (myListeners) {
      myListeners.add(listener);
    }
  }

  public void notifyListeners() {
    synchronized (myListeners) {
      for (Listener listener : myListeners) {
        listener.logChanged();
      }
    }
  }

}


package com.jetbrains.python.debugger.threading;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebugSessionListener;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.impl.XSourcePositionImpl;
import com.jetbrains.python.debugger.PyLockEvent;
import com.jetbrains.python.debugger.PyThreadingEvent;
import com.jetbrains.python.debugger.threading.tool.graph.ui.LockOwnThreadState;
import com.jetbrains.python.debugger.threading.tool.graph.ui.LockWaitThreadState;
import com.jetbrains.python.debugger.threading.tool.graph.ui.RunThreadState;
import com.jetbrains.python.debugger.threading.tool.graph.ui.ThreadState;
import com.jetbrains.python.debugger.threading.tool.ui.ThreadingNamesManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PyThreadingLogManagerImpl extends PyThreadingLogManager {
  private final Project myProject;
  private XDebugSession lastSession;
  private List<PyThreadingEvent> myLog;
  private final List<Listener> myListeners;
  public ThreadingNamesManager myLockManager;

  public PyThreadingLogManagerImpl(Project project) {
    myProject = project;
    myLog = new ArrayList<PyThreadingEvent>();
    myListeners = new ArrayList<Listener>();
    myLockManager = new ThreadingNamesManager();
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

  public ThreadState getThreadStateAt(int index, String threadId) {
    int locksAcquired = 0;
    int locksOwn = 0;
    for (int i = 0; i <= index; ++i) {
      PyThreadingEvent event = myLog.get(i);
      if ((event.getThreadId().equals(threadId) && event instanceof PyLockEvent)) {
        PyLockEvent lockEvent = (PyLockEvent)event;
        if (lockEvent.getType() == PyLockEvent.EventType.ACQUIRE_BEGIN) {
          locksAcquired++;
        }
        if (lockEvent.getType() == PyLockEvent.EventType.ACQUIRE_END) {
          locksOwn++;
        }
        if (lockEvent.getType() == PyLockEvent.EventType.RELEASE) {
          locksAcquired--;
          locksOwn--;
        }
      }
    }
    if (locksOwn > 0) {
      return new LockOwnThreadState();
    }
    if (locksAcquired > 0) {
      return new LockWaitThreadState();
    }
    return new RunThreadState();
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


package com.jetbrains.python.debugger.concurrency;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.impl.XSourcePositionImpl;
import com.jetbrains.python.debugger.PyLockEvent;
import com.jetbrains.python.debugger.PyThreadingEvent;
import com.jetbrains.python.debugger.concurrency.tool.threading.graph.ui.LockOwnThreadState;
import com.jetbrains.python.debugger.concurrency.tool.threading.graph.ui.LockWaitThreadState;
import com.jetbrains.python.debugger.concurrency.tool.threading.graph.ui.RunThreadState;
import com.jetbrains.python.debugger.concurrency.tool.threading.graph.ui.ThreadState;
import com.jetbrains.python.debugger.concurrency.tool.threading.ThreadingNamesManager;

import java.util.ArrayList;

public class PyThreadingLogManagerImpl extends PyConcurrencyLogManager<PyThreadingEvent> {
  public ThreadingNamesManager myLockManager;

  public PyThreadingLogManagerImpl(Project project) {
    myProject = project;
    myLog = new ArrayList<PyThreadingEvent>();
    myListeners = new ArrayList<Listener>();
    myLockManager = new ThreadingNamesManager();
  }

  public String getThreadIdForEventAt(int index) {
    return myLog.get(index).getThreadId();
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


  public XSourcePosition getSourcePositionForEventNumber(int num) {
    final PyThreadingEvent event = myLog.get(num);
    VirtualFile vFile = LocalFileSystem.getInstance().findFileByPath(event.getFileName());
    return XSourcePositionImpl.create(vFile, event.getLine());
  }

}

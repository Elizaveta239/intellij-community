
package com.jetbrains.python.debugger.concurrency.tool.threading;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.jetbrains.python.debugger.PyConcurrencyEvent;
import com.jetbrains.python.debugger.PyLockEvent;
import com.jetbrains.python.debugger.concurrency.PyConcurrencyLogManager;
import com.jetbrains.python.debugger.concurrency.tool.ConcurrencyNamesManager;
import com.jetbrains.python.debugger.concurrency.tool.graph.LockOwnThreadState;
import com.jetbrains.python.debugger.concurrency.tool.graph.LockWaitThreadState;
import com.jetbrains.python.debugger.concurrency.tool.graph.RunThreadState;
import com.jetbrains.python.debugger.concurrency.tool.graph.ThreadState;

import java.util.ArrayList;

public class PyThreadingLogManagerImpl extends PyConcurrencyLogManager<PyConcurrencyEvent> {
  public ConcurrencyNamesManager myLockManager;

  public static PyThreadingLogManagerImpl getInstance(Project project) {
    return ServiceManager.getService(project, PyThreadingLogManagerImpl.class);
  }

  public PyThreadingLogManagerImpl(Project project) {
    myProject = project;
    myLog = new ArrayList<PyConcurrencyEvent>();
    myLockManager = new ConcurrencyNamesManager();
  }

  public String getThreadIdForEventAt(int index) {
    return myLog.get(index).getThreadId();
  }

  public ThreadState getThreadStateAt(int index, String threadId) {
    int locksAcquired = 0;
    int locksOwn = 0;
    for (int i = 0; i <= index; ++i) {
      PyConcurrencyEvent event = myLog.get(i);
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

}

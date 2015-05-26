
package com.jetbrains.python.debugger.concurrency.tool.threading;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.jetbrains.python.debugger.PyConcurrencyEvent;
import com.jetbrains.python.debugger.concurrency.PyConcurrencyLogManager;
import com.jetbrains.python.debugger.concurrency.tool.ConcurrencyNamesManager;
import com.jetbrains.python.debugger.concurrency.tool.ConcurrencyStat;

import java.util.ArrayList;
import java.util.HashMap;


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

  @Override
  public HashMap getStatistics() {
    HashMap<String, ConcurrencyStat> result = new HashMap<String, ConcurrencyStat>();
    for (PyConcurrencyEvent event: myLog) {
      String threadId = event.getThreadName();
      if (event.isThreadEvent() && event.getType() == PyConcurrencyEvent.EventType.START) {
        ConcurrencyStat stat = new ConcurrencyStat(event.getTime());
        result.put(threadId, stat);
      } else if (event.getType() == PyConcurrencyEvent.EventType.STOP) {
        ConcurrencyStat stat = new ConcurrencyStat(event.getTime());
        stat.myFinishTime = event.getTime();
      } else if (event.getType() == PyConcurrencyEvent.EventType.ACQUIRE_BEGIN) {
        ConcurrencyStat stat = result.get(threadId);
        stat.myLockCount++;
        stat.myLastAcquireStartTime = event.getTime();
      } else if (event.getType() == PyConcurrencyEvent.EventType.ACQUIRE_END) {
        ConcurrencyStat stat = result.get(threadId);
        stat.myWaitTime += (event.getTime() - stat.myLastAcquireStartTime);
        stat.myLastAcquireStartTime = 0;
      }
    }
    PyConcurrencyEvent lastEvent = myLog.get(myLog.size() - 1);
    int lastTime = lastEvent.getTime();
    //set last time for stopping on a breakpoint
    for (ConcurrencyStat stat: result.values()) {
      if (stat.myFinishTime == 0) {
        stat.myFinishTime = lastTime;
      }
    }
    return result;
  }

}

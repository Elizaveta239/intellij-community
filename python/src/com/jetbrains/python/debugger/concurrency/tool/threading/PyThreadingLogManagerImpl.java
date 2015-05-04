
package com.jetbrains.python.debugger.concurrency.tool.threading;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.jetbrains.python.debugger.PyConcurrencyEvent;
import com.jetbrains.python.debugger.concurrency.PyConcurrencyLogManager;
import com.jetbrains.python.debugger.concurrency.tool.ConcurrencyNamesManager;

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

}

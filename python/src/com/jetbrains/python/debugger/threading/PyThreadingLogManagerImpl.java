
package com.jetbrains.python.debugger.threading;

import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebugSession;
import com.jetbrains.python.debugger.PyThreadingEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PyThreadingLogManagerImpl extends PyThreadingLogManager {
  private final Project myProject;
  private XDebugSession lastSession;
  private List<PyThreadingEvent> myLog;

  public PyThreadingLogManagerImpl(Project project) {
    myProject = project;
    myLog = new ArrayList<PyThreadingEvent>();
  }

  public List<PyThreadingEvent> getLog() {
    return myLog;
  }

  public String getStringRepresentation() {
    StringBuilder resultBuilder = new StringBuilder();
    resultBuilder.append("Size: ").append(myLog.size()).append("\n");
    for (PyThreadingEvent event: getLog()) {
      resultBuilder.append(event.toString());
    }
    return resultBuilder.toString();
  }

  @Override
  public void recordEvent(@NotNull XDebugSession debugSession, @NotNull PyThreadingEvent event) {
    if ((lastSession == null) || (debugSession != lastSession)) {
      lastSession = debugSession;
      myLog = new ArrayList<PyThreadingEvent>();
    }
    myLog.add(event);
  }
}


package com.jetbrains.python.debugger.threading;

import com.intellij.openapi.project.Project;
import com.jetbrains.python.debugger.PyThreadingEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PyThreadingLogManagerImpl extends PyThreadingLogManager {
  private final Project myProject;
  private List<PyThreadingEvent> myLog;

  public PyThreadingLogManagerImpl(Project project) {
    myProject = project;
    myLog = new ArrayList<PyThreadingEvent>();
  }

  @Override
  public void recordEvent(@NotNull PyThreadingEvent event) {
    myLog.add(event);
  }
}

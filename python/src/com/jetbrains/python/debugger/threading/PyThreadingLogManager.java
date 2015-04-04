
package com.jetbrains.python.debugger.threading;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebugSession;
import com.jetbrains.python.debugger.PyThreadingEvent;
import org.jetbrains.annotations.NotNull;

public abstract class PyThreadingLogManager {
  public static PyThreadingLogManager getInstance(Project project) {
    return ServiceManager.getService(project, PyThreadingLogManager.class);
  }

  public abstract void recordEvent(@NotNull XDebugSession debugProcess, PyThreadingEvent event);

  public abstract String getStringRepresentation();

}

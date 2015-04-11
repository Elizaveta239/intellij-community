
package com.jetbrains.python.debugger.concurrency;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebugSessionListener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class PyConcurrencyLogManager<T> {
  protected List<T> myLog;
  protected List<Listener> myListeners;
  protected XDebugSession lastSession;
  protected Project myProject;

  public static PyConcurrencyLogManager getInstance(Project project) {
    return ServiceManager.getService(project, PyConcurrencyLogManager.class);
  }

  public Integer getSize() {
    return myLog.size();
  }

  public T getEventAt(int index) {
    return myLog.get(index);
  }

  public List<T> getLog() {
    return myLog;
  }

  public String getStringRepresentation() {
    StringBuilder resultBuilder = new StringBuilder();
    resultBuilder.append("<html>Size: ").append(myLog.size()).append("<br>");
    for (T event: getLog()) {
      resultBuilder.append(event.toString());
    }
    resultBuilder.append("</html>");
    return resultBuilder.toString();
  }

  public void recordEvent(@NotNull XDebugSession debugSession, T event) {
    if (((lastSession == null) || (debugSession != lastSession)) && event == null) {
      lastSession = debugSession;
      myLog = new ArrayList<T>();
      addSessionListener();
      return;
    }
    myLog.add(event);
    notifyListeners();
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

/*
 * Copyright 2000-2013 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.remoteServer.util;

import com.intellij.execution.process.ProcessHandler;
import com.intellij.remoteServer.agent.util.CloudAgentLoggingHandler;
import com.intellij.remoteServer.agent.util.log.LogListener;
import com.intellij.remoteServer.runtime.deployment.DeploymentLogManager;
import com.intellij.remoteServer.runtime.log.LoggingHandler;
import org.jetbrains.annotations.Nullable;

import java.io.OutputStream;
import java.util.HashMap;

/**
 * @author michael.golubev
 */
public class CloudLoggingHandlerImpl implements CloudAgentLoggingHandler {

  private final HashMap<String, LogListener> myPipeName2LogListener;

  private final LoggingHandler myMainLoggingHandler;

  private final DeploymentLogManager myLogManager;

  public CloudLoggingHandlerImpl(DeploymentLogManager logManager) {
    myMainLoggingHandler = logManager.getMainLoggingHandler();
    myPipeName2LogListener = new HashMap<String, LogListener>();
    myLogManager = logManager;
  }

  @Override
  public void println(String message) {
    myMainLoggingHandler.print(message + "\n");
  }

  @Override
  public LogListener getOrCreateLogListener(String pipeName) {
    LogListener logListener = myPipeName2LogListener.get(pipeName);
    if (logListener == null) {
      final LoggingHandler loggingHandler = myLogManager.addAdditionalLog(pipeName);
      logListener = new LogListener() {

        @Override
        public void lineLogged(String line) {
          loggingHandler.print(line + "\n");
        }
      };
      myPipeName2LogListener.put(pipeName, logListener);
    }
    return logListener;
  }

  @Override
  public LogListener createConsole(String pipeName, final OutputStream consoleInput) {
    final LoggingHandler loggingHandler = myLogManager.addAdditionalLog(pipeName);
    loggingHandler.attachToProcess(new ProcessHandler() {

      @Override
      protected void destroyProcessImpl() {

      }

      @Override
      protected void detachProcessImpl() {

      }

      @Override
      public boolean detachIsDefault() {
        return false;
      }

      @Nullable
      @Override
      public OutputStream getProcessInput() {
        return consoleInput;
      }
    });

    return new LogListener() {

      @Override
      public void lineLogged(String line) {
        loggingHandler.print(line);
      }
    };
  }
}

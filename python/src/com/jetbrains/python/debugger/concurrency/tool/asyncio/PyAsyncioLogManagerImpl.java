/*
 * Copyright 2000-2015 JetBrains s.r.o.
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
package com.jetbrains.python.debugger.concurrency.tool.asyncio;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.jetbrains.python.debugger.PyConcurrencyEvent;
import com.jetbrains.python.debugger.PyLockEvent;
import com.jetbrains.python.debugger.concurrency.PyConcurrencyLogManager;
import com.jetbrains.python.debugger.concurrency.tool.graph.LockOwnThreadState;
import com.jetbrains.python.debugger.concurrency.tool.graph.LockWaitThreadState;
import com.jetbrains.python.debugger.concurrency.tool.graph.RunThreadState;
import com.jetbrains.python.debugger.concurrency.tool.graph.ThreadState;

import java.util.ArrayList;

public class PyAsyncioLogManagerImpl extends PyConcurrencyLogManager<PyConcurrencyEvent> {

  public static PyAsyncioLogManagerImpl getInstance(Project project) {
    return ServiceManager.getService(project, PyAsyncioLogManagerImpl.class);
  }

  public String getTaskIdForEventAt(int index) {
    return myLog.get(index).getThreadId();
  }

  public PyAsyncioLogManagerImpl(Project project) {
    myProject = project;
    myLog = new ArrayList<PyConcurrencyEvent>();
  }

}

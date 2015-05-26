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
import com.jetbrains.python.debugger.concurrency.PyConcurrencyLogManager;
import com.jetbrains.python.debugger.concurrency.tool.ConcurrencyStat;

import java.util.ArrayList;
import java.util.HashMap;

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

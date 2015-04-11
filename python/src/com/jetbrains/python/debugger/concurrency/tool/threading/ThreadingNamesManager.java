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
package com.jetbrains.python.debugger.concurrency.tool.threading;


import com.intellij.util.containers.hash.HashMap;
import com.jetbrains.python.debugger.PyLockEvent;
import com.jetbrains.python.debugger.PyThreadEvent;
import com.jetbrains.python.debugger.PyThreadingEvent;

import java.util.Map;

public class ThreadingNamesManager {
  private Map<String, String> myLockMap;
  private int lastNumber = 0;

  public ThreadingNamesManager() {
    myLockMap = new HashMap<String, String>();
  }

  private String getLockNameById(String lockId) {
    if (myLockMap.containsKey(lockId)) {
      return myLockMap.get(lockId);
    } else {
      lastNumber++;
      String newName = "Lock-" + lastNumber;
      myLockMap.put(lockId, newName);
      return newName;
    }
  }

  public String getFullEventName(PyThreadingEvent event) {
    StringBuilder sb = new StringBuilder();
    if (event instanceof PyThreadEvent) {
      sb.append(event.getThreadName());
      sb.append(" ");
      sb.append(event.getEventActionName());
      return sb.toString();
    }

    if (event instanceof PyLockEvent) {
      sb.append(getLockNameById(((PyLockEvent)event).getId()));
      sb.append(" ");
      sb.append(event.getEventActionName());
      return sb.toString();
    }

    return "Incorrect event";
  }
}

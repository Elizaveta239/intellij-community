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
package com.jetbrains.python.debugger.concurrency.tool.graph;

import com.intellij.util.containers.HashSet;
import com.intellij.util.containers.hash.HashMap;
import com.jetbrains.python.debugger.PyConcurrencyEvent;
import com.jetbrains.python.debugger.PyLockEvent;
import com.jetbrains.python.debugger.concurrency.PyConcurrencyLogManager;
import com.jetbrains.python.debugger.concurrency.tool.graph.states.LockOwnThreadState;
import com.jetbrains.python.debugger.concurrency.tool.graph.states.LockWaitThreadState;
import com.jetbrains.python.debugger.concurrency.tool.graph.states.RunThreadState;
import com.jetbrains.python.debugger.concurrency.tool.graph.states.ThreadState;
import org.jetbrains.annotations.Nullable;


public class GraphAnalyser {
  private final PyConcurrencyLogManager<PyConcurrencyEvent> myLogManager;
  private HashMap<String, ThreadLocksInfo> myLocksInfo;
  private int lastInd = 0;

  public GraphAnalyser(PyConcurrencyLogManager<PyConcurrencyEvent> logManager) {
    myLogManager = logManager;
  }

  private class ThreadLocksInfo {
    private String lockWait;
    private HashSet<String> locksOwn;

    public ThreadLocksInfo() {
      locksOwn = new HashSet<String>();
    }

    public void setLockWait(@Nullable String lockWait) {
      this.lockWait = lockWait;
    }

    public void addOwn(String lockId) {
      locksOwn.add(lockId);
    }

    public boolean removeOwn(String lockId) {
      return locksOwn.remove(lockId);
    }

    public String getLockWait() {
      return lockWait;
    }

    public boolean isOwning(String lockId) {
      return locksOwn.contains(lockId);
    }
  }

  public void updateLocksInfo(int ind) {
    if (lastInd == 0) {
      myLocksInfo = new HashMap<String, ThreadLocksInfo>();
    }
    for (int i = lastInd; i <= ind; ++i) {
      PyConcurrencyEvent event = myLogManager.getEventAt(i);
      if (event instanceof PyLockEvent) {
        PyLockEvent lockEvent = (PyLockEvent)event;
        String threadId = lockEvent.getThreadId();
        String lockId = lockEvent.getId();
        if (!myLocksInfo.containsKey(threadId)) {
          myLocksInfo.put(threadId, new ThreadLocksInfo());
        }
        ThreadLocksInfo info = myLocksInfo.get(threadId);
        if (lockEvent.getType() == PyLockEvent.EventType.ACQUIRE_BEGIN) {
          info.setLockWait(lockId);
        }
        if (lockEvent.getType() == PyLockEvent.EventType.ACQUIRE_END) {
          info.setLockWait(null);
          info.addOwn(lockId);
        }
        if (lockEvent.getType() == PyLockEvent.EventType.RELEASE) {
          info.removeOwn(lockId);
        }
      }
    }
    lastInd = ind;
  }

  @Nullable
  public HashSet<String> checkForDeadlocks(int ind) {
    if (lastInd != ind) {
      updateLocksInfo(ind);
    }
    PyConcurrencyEvent event = myLogManager.getEventAt(ind);
    String startThreadId = event.getThreadId();
    String startWait = myLocksInfo.get(startThreadId).getLockWait();
    if (startWait == null) {
      return null;
    }
    HashSet<String> threadsInsideDeadlock = new HashSet<String>();
    threadsInsideDeadlock.add(startThreadId);

    String wait = startWait;
    while (wait != null) {
      boolean isOwnedBySmb = false;
      for (String thread: myLocksInfo.keySet()) {
        ThreadLocksInfo info = myLocksInfo.get(thread);
        if (info.isOwning(wait) && (info.getLockWait() != null)) {
          if (threadsInsideDeadlock.contains(thread)) {
            return threadsInsideDeadlock;
          }
          threadsInsideDeadlock.add(thread);
          wait = info.getLockWait();
          isOwnedBySmb = true;
        }
      }
      if (!isOwnedBySmb) {
        return null;
      }
    }
    return null;
  }

  public ThreadState getThreadStateAt(int index, String threadId) {
    HashSet<String> locksAcquired = new HashSet<String>();
    HashSet<String> locksOwn = new HashSet<String>();
    for (int i = 0; i <= index; ++i) {
      PyConcurrencyEvent event = myLogManager.getEventAt(i);
      if ((event.getThreadId().equals(threadId) && event instanceof PyLockEvent)) {
        PyLockEvent lockEvent = (PyLockEvent)event;
        if (lockEvent.getType() == PyLockEvent.EventType.ACQUIRE_BEGIN) {
          locksAcquired.add(lockEvent.getId());
        }
        if (lockEvent.getType() == PyLockEvent.EventType.ACQUIRE_END) {
          locksAcquired.remove(lockEvent.getId());
          locksOwn.add(lockEvent.getId());
        }
        if (lockEvent.getType() == PyLockEvent.EventType.RELEASE) {
          locksOwn.remove(lockEvent.getId());
        }
      }
    }
    if (locksAcquired.size() > 0) {
      return new LockWaitThreadState();
    }
    if (locksOwn.size() > 0) {
      return new LockOwnThreadState();
    }
    return new RunThreadState();
  }
}

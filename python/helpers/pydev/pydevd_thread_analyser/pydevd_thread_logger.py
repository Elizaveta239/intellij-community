
from pydevd_constants import DictContains, GetThreadId
from pydevd_file_utils import GetFilenameAndBase
from pydevd_thread_analyser.pydevd_thread_wrappers import LockWrapper

import _pydev_threading as threading
threadingCurrentThread = threading.currentThread


DONT_TRACE_THREADING = ['threading.py']
THREAD_METHODS = ['start', 'join']
LOCK_METHODS = ['acquire', 'release']
LOCK_CONTEXT_MGR = ['__enter__', '__exit__']


def log_event(frame):
    write_log = False
    self_obj = None
    if DictContains(frame.f_locals, "self"):
        self_obj = frame.f_locals["self"]
        if isinstance(self_obj, threading.Thread) or self_obj.__class__ == LockWrapper:
            write_log = True

    if write_log:
        t = threadingCurrentThread()
        back = frame.f_back
        name, back_base = GetFilenameAndBase(back)
        if back_base not in DONT_TRACE_THREADING:
            method_name = frame.f_code.co_name
            if isinstance(self_obj, threading.Thread) and method_name in THREAD_METHODS:
                print(GetThreadId(self_obj), back_base, back.f_lineno, frame.f_code.co_name)
            elif self_obj.__class__ == LockWrapper:
                if DictContains(frame.f_locals, "attr") and frame.f_locals["attr"] in LOCK_METHODS:
                    print(GetThreadId(t), back_base, back.f_lineno, frame.f_locals["attr"])
                elif method_name in LOCK_CONTEXT_MGR:
                    print(GetThreadId(t), back_base, back.f_lineno, frame.f_code.co_name)
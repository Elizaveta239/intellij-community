
import _pydev_threading as threading
from pydevd_constants import GetThreadId
from _pydev_threading import currentThread


def wrapper(fun):
    def inner(*args, **kwargs):
        print("start_new_thread called with params", args, kwargs)
        thread = threading.currentThread()
        thread.additionalInfo.save = True
        return fun(*args, **kwargs)
    return inner


class LockWrapper(object):
    def __init__(self, object):
        self.wrapped_object = object

    def __getattr__(self, attr):
        orig_attr = self.wrapped_object.__getattribute__(attr)
        if callable(orig_attr):
            def hooked(*args, **kwargs):
                result = orig_attr(*args, **kwargs)
                if result == self.wrapped_object:
                    return self
                return result
            return hooked
        else:
            return orig_attr

    def __enter__(self):
        self.wrapped_object.__enter__()

    def __exit__(self, exc_type, exc_val, exc_tb):
        self.wrapped_object.__exit__(exc_type, exc_val, exc_tb)


def factory_wrapper(fun):
    def inner(*args, **kwargs):
        obj = fun(*args, **kwargs)
        return LockWrapper(obj)
    return inner


def wrap_threads():
    # TODO: add wrappers for thread and _thread
    # import _thread as mod
    # print("Thread imported")
    # mod.start_new_thread = wrapper(mod.start_new_thread)
    import threading
    threading.Lock = factory_wrapper(threading.Lock)
    threading.RLock = factory_wrapper(threading.RLock)

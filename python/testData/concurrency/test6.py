from threading import Thread, Lock
import time


def f(timeout, lock1, lock2):
    with lock1:
        time.sleep(1)
        print('lock acquired')
        with lock2:
            print('i am task number {}'.format(timeout))
    print('done')

lock1 = Lock()
lock2 = Lock()

threads = [Thread(target=f, args=(1, lock1, lock2)),
           Thread(target=f, args=(2, lock2, lock1))]

for thread in threads:
    thread.start()

for thread in threads:
    thread.join()
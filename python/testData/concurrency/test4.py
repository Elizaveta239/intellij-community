from threading import Thread, Lock
from time import sleep

def f(number, lock):
    with lock:
        print('got number {}'.format(number))

lock = Lock()
threads = [Thread(target=f, args=(i, lock)) for i in range(1, 3)]

threads[0].start()
sleep(1)
threads[1].start()
sleep(1)

for thread in threads:
    thread.join()
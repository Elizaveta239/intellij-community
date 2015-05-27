import asyncio
from asyncio import Lock

lock = Lock()

@asyncio.coroutine
def factorial(name, number):
    yield from lock
    try:
        print("Task %s: number = %s" % (name, number))
    finally:
        lock.release()

loop = asyncio.get_event_loop()
tasks = [
    asyncio.async(factorial("A", 2)),
    asyncio.async(factorial("B", 3))]
loop.run_until_complete(asyncio.wait(tasks))
loop.close()
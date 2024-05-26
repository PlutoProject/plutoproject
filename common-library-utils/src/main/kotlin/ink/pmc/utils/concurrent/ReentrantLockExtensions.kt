package ink.pmc.utils.concurrent

import java.util.concurrent.locks.ReentrantLock

fun <T> ReentrantLock.withLock(block: () -> T): T {
    this.lock()
    val value = block.invoke()
    this.unlock()
    return value
}
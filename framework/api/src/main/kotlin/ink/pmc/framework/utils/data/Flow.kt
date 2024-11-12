package ink.pmc.framework.utils.data

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

@Suppress("NOTHING_TO_INLINE", "FunctionName")
inline fun <T> NoReplayNotifyFlow() = MutableSharedFlow<T>(onBufferOverflow = BufferOverflow.DROP_LATEST)
package ink.pmc.framework.time

import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

inline val Int.ticks: Duration get() = (this * 50).toDuration(DurationUnit.MILLISECONDS)

inline val Long.ticks: Duration get() = (this * 50).toDuration(DurationUnit.MILLISECONDS)
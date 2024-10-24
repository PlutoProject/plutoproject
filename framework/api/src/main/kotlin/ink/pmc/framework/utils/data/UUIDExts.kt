package ink.pmc.framework.utils.data

import java.util.*

@Suppress("UNUSED")
val UUID.trimmed: String
    get() = this.toString().replace("-", "")
package ink.pmc.playerdb

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

var disabled = false
var playerDbScope = CoroutineScope(Dispatchers.IO)
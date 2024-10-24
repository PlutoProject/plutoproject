package ink.pmc.framework.utils.network

import java.net.InetSocketAddress

inline val InetSocketAddress.formatted: String
    get() = "${hostString}:${port}"
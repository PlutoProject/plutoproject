package ink.pmc.framework.network

import java.net.InetSocketAddress

inline val InetSocketAddress.formatted: String
    get() = "${hostString}:${port}"
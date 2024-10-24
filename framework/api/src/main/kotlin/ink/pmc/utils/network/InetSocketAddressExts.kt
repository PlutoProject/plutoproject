package ink.pmc.utils.network

import java.net.InetSocketAddress

inline val InetSocketAddress.formatted: String
    get() = "${hostString}:${port}"
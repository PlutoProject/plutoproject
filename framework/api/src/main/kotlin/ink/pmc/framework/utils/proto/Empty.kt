package ink.pmc.framework.utils.proto

import com.google.protobuf.Empty

inline val empty: Empty
    get() = Empty.getDefaultInstance()
package ink.pmc.framework.proto

import com.google.protobuf.Empty

inline val empty: Empty
    get() = Empty.getDefaultInstance()
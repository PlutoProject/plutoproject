package ink.pmc.utils.proto

import com.google.protobuf.Empty

inline val empty: Empty
    get() = Empty.getDefaultInstance()
package ink.pmc.common.member

import com.google.protobuf.Empty
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import ink.pmc.common.member.proto.MemberRpcGrpcKt
import ink.pmc.common.member.proto.MemberUpdateNotifyOuterClass.MemberUpdateNotify
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class VelocityMemberService(database: MongoDatabase) : BaseMemberServiceImpl(database) {

    private val monitorFlow = MutableSharedFlow<MemberUpdateNotify>()
    val rpcService  = object : MemberRpcGrpcKt.MemberRpcCoroutineImplBase() {
        override suspend fun notifyMemberUpdate(request: MemberUpdateNotify): Empty {
            handleUpdate(request)
            notifyUpdate(request)
            return Empty.getDefaultInstance()
        }

        override fun monitorMemberUpdate(request: Empty): Flow<MemberUpdateNotify> {
            return monitorFlow
        }
    }

    override suspend fun notifyUpdate(notify: MemberUpdateNotify) {
        monitorFlow.emit(notify)
    }

    override suspend fun monitorUpdate() {
        return
    }

}
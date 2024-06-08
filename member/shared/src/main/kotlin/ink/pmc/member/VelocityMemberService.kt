package ink.pmc.member

import com.google.protobuf.Empty
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import ink.pmc.member.proto.MemberRpcGrpcKt
import ink.pmc.member.proto.MemberUpdateNotifyOuterClass.MemberUpdateNotify
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.*

class VelocityMemberService(database: MongoDatabase) : BaseMemberServiceImpl(database) {

    private val monitorFlow = MutableSharedFlow<MemberUpdateNotify>()
    val rpcService = object : MemberRpcGrpcKt.MemberRpcCoroutineImplBase() {
        override suspend fun notifyMemberUpdate(request: MemberUpdateNotify): Empty {
            notifyUpdate(request)
            handleUpdate(request)
            return Empty.getDefaultInstance()
        }

        override fun monitorMemberUpdate(request: Empty): Flow<MemberUpdateNotify> {
            return monitorFlow
        }
    }

    override suspend fun notifyUpdate(notify: MemberUpdateNotify) {
        if (UUID.fromString(notify.serviceId) == id) {
            serverLogger.info("Sending notify (serviceId=${notify.serviceId}) to servers behind proxy...")
        } else {
            serverLogger.info("Forwarding notify (serviceId=${notify.serviceId}) to servers behind proxy...")
        }

        monitorFlow.emit(notify)
    }

    override suspend fun monitorUpdate() {
        return
    }

}
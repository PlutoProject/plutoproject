package ink.pmc.member

import com.google.protobuf.Empty
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import ink.pmc.member.proto.MemberRpcGrpcKt
import ink.pmc.member.proto.MemberUpdateNotifyOuterClass.MemberUpdateNotify
import ink.pmc.rpc.api.RpcClient

class PaperMemberService(database: MongoDatabase) : BaseMemberServiceImpl(database) {

    private lateinit var stub: MemberRpcGrpcKt.MemberRpcCoroutineStub

    override suspend fun notifyUpdate(notify: MemberUpdateNotify) {
        serverLogger.info("Sending an update notify to proxy for UID ${notify.memberId}...")
        stub.notifyMemberUpdate(notify)
    }

    override suspend fun monitorUpdate() {
        if (!::stub.isInitialized) {
            stub = MemberRpcGrpcKt.MemberRpcCoroutineStub(RpcClient.channel)
        }

        stub.monitorMemberUpdate(Empty.getDefaultInstance()).collect {
            handleUpdate(it)
        }
    }
}
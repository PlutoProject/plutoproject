package ink.pmc.common.member

import com.google.protobuf.Empty
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import ink.pmc.common.member.proto.MemberRpcGrpcKt
import ink.pmc.common.member.proto.MemberUpdateNotifyOuterClass.MemberUpdateNotify
import ink.pmc.common.rpc.RpcClient

class PaperMemberService(database: MongoDatabase) : BaseMemberServiceImpl(database) {

    private val stub = RpcClient.stub(MemberRpcGrpcKt.MemberRpcCoroutineStub::class)

    override suspend fun notifyUpdate(notify: MemberUpdateNotify) {
        stub.notifyMemberUpdate(notify)
    }

    override suspend fun monitorUpdate() {
        stub.monitorMemberUpdate(Empty.getDefaultInstance()).collect {
            handleUpdate(it)
        }
    }

}
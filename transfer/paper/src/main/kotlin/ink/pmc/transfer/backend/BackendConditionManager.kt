package ink.pmc.transfer.backend

import ink.pmc.transfer.api.ConditionManager
import ink.pmc.transfer.api.Destination
import ink.pmc.transfer.proto.ConditionVerify.ConditionVerifyResult
import ink.pmc.transfer.proto.TransferRpcGrpcKt.TransferRpcCoroutineStub
import ink.pmc.transfer.proto.conditionVerifyReq
import ink.pmc.utils.multiplaform.player.PlayerWrapper

class BackendConditionManager(private val stub: TransferRpcCoroutineStub) : ConditionManager {

    override suspend fun verifyCondition(player: PlayerWrapper<*>, destination: Destination): Boolean {
        val result = stub.verifyCondition(conditionVerifyReq {
            uuid = player.uuid.toString()
            this.destination = destination.id
        }).result
        return result == ConditionVerifyResult.VERIFY_SUCCEED
    }

}
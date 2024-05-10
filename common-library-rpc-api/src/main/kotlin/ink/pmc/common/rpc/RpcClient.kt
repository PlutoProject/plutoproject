package ink.pmc.common.rpc

import io.grpc.kotlin.AbstractCoroutineStub
import kotlin.reflect.KClass

object RpcClient : IRpcClient by IRpcClient.instance

interface IRpcClient {

    companion object {
        lateinit var instance: IRpcClient
    }

    fun <T : AbstractCoroutineStub<T>> stub(cls: KClass<T>): T

}
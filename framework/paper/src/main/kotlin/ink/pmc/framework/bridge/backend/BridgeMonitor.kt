package ink.pmc.framework.bridge.backend

import ink.pmc.framework.bridge.proto.BridgeRpcGrpcKt.BridgeRpcCoroutineStub
import ink.pmc.framework.rpc.RpcClient

val bridgeStub = BridgeRpcCoroutineStub(RpcClient.channel)
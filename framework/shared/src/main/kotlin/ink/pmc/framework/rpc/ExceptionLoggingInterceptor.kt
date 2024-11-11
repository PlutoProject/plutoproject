package ink.pmc.framework.rpc

import ink.pmc.framework.frameworkLogger
import io.grpc.*
import java.util.logging.Level

object ExceptionLoggingInterceptor : ServerInterceptor {
    override fun <ReqT : Any?, RespT : Any?> interceptCall(
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        next: ServerCallHandler<ReqT, RespT>
    ): ServerCall.Listener<ReqT> {
        val listener = next.startCall(call, headers)
        return object : ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(listener) {
            override fun onHalfClose() {
                try {
                    super.onHalfClose()
                } catch (e: Exception) {
                    frameworkLogger.log(
                        Level.SEVERE,
                        "Exception in RPC call: ${call.methodDescriptor.fullMethodName}",
                        e
                    )
                    call.close(Status.INTERNAL.withDescription("Internal server error").withCause(e), headers)
                    throw e
                }
            }
        }
    }
}
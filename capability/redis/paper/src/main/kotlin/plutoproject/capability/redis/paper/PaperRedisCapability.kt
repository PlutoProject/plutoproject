package plutoproject.capability.redis.paper

import plutoproject.capability.redis.common.RedisCapability
import plutoproject.kernel.api.Capability
import plutoproject.kernel.api.Platform
import plutoproject.kernel.api.RuntimeModule

@Capability(id = "redis", platform = Platform.PAPER)
class PaperRedisCapability : RuntimeModule by RedisCapability()

package plutoproject.capability.redis.velocity

import plutoproject.capability.redis.common.RedisCapability
import plutoproject.kernel.api.Capability
import plutoproject.kernel.api.Platform
import plutoproject.kernel.api.RuntimeModule

@Capability(id = "redis", platform = Platform.VELOCITY)
class VelocityRedisCapability : RuntimeModule by RedisCapability()

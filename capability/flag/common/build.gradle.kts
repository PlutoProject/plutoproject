plugins {
    id("plutoproject.core")
    id("plutoproject.test")
}

dependencies {
    implementation(projects.kernel.api)
    implementation(projects.capability.flag.api)
    implementation(projects.capability.redis.api)
    implementation(projects.capability.serverIdentifier.api)
    implementation(libs.bundles.hoplite)
    implementation(libs.koin.core)
    implementation(libs.kotlinx.coroutine.reactive)
    testImplementation(platform(libs.testcontainers.bom))
    testImplementation(libs.testcontainers)
}

// 本机 Docker 服务未启用时，退回 rootless podman 的 socket。
val podmanSocket = File(
    "/run/user/${Runtime.getRuntime().exec(arrayOf("id", "-u")).inputStream.bufferedReader().readText().trim()}/podman/podman.sock",
)
tasks.test {
    if (System.getenv("DOCKER_HOST") == null && podmanSocket.exists()) {
        environment("DOCKER_HOST", "unix://${podmanSocket.absolutePath}")
    }
    // ryuk 需要特权容器，rootless podman 下容易失败；清理由测试自己负责。
    environment("TESTCONTAINERS_RYUK_DISABLED", "true")
}

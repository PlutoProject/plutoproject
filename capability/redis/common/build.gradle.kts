plugins {
    id("plutoproject.core")
    id("plutoproject.test")
}

dependencies {
    implementation(projects.kernel.api)
    implementation(projects.capability.redis.api)
    implementation(libs.bundles.hoplite)
    implementation(libs.koin.core)
}

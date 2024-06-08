pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "plutoproject"

include("bedrock-adaptive")
include("bedrock-adaptive:velocity")

include("dependency-loader")
include("dependency-loader:paper")
include("dependency-loader:velocity")

include("utils")
include("utils:paper")
include("utils:velocity")
include("utils:api")

include("member")
include("member:paper")
include("member:api")
include("member:velocity")
include("member:proto")
include("member:shared")

include("rpc")
include("rpc:api")
include("rpc:paper")
include("rpc:velocity")
include("rpc:shared")

include("misc")
include("misc:api")
include("misc:paper")

include("hypervisor")
include("hypervisor:paper")

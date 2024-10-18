pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.jpenilla.xyz/snapshots")
        mavenLocal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "plutoproject"

// 删除 1.2.0
/*
include("bedrock-adaptive")
include("bedrock-adaptive:velocity")
*/

include("dependency-loader")
include("dependency-loader:paper")
include("dependency-loader:velocity")

include("utils")
include("utils:paper")
include("utils:velocity")
include("utils:api")
include("utils:proto")

// 删除 1.2.0
/*
include("member")
include("member:paper")
include("member:api")
include("member:velocity")
include("member:proto")
include("member:shared")
*/

include("rpc")
include("rpc:api")
include("rpc:paper")
include("rpc:velocity")
include("rpc:shared")

include("misc")
include("misc:api")
include("misc:paper")

include("hypervisor")
include("hypervisor:api")
include("hypervisor:paper")

// 删除：1.1.0
/*
include("exchange")
include("exchange:api")
include("exchange:paper")
include("exchange:shared")
include("exchange:velocity")
include("exchange:proto")
*/

include("visual")
include("visual:api")
include("visual:paper")
include("visual:shared")

include("provider")
include("provider:api")
include("provider:paper")
include("provider:velocity")
include("provider:shared")

// 删除：1.1.0
/*
include("transfer")
include("transfer:api")
include("transfer:paper")
include("transfer:velocity")
include("transfer:shared")
include("transfer:proto")
*/

// 删除：1.2.0
/*
include("messages")
include("messages:paper")
include("messages:velocity")
*/

include("protocol-checker")
include("protocol-checker:velocity")

include("essentials")
include("essentials:paper")
include("essentials:api")

include("interactive")
include("interactive:api")
include("interactive:paper")
include("interactive:velocity")

include("menu")
include("menu:paper")

include("bridge")
include("bridge:api")
include("bridge:proto")
include("bridge:paper")
include("bridge:velocity")

include("daily")
include("daily:api")
include("daily:paper")

include("whitelist")
include("whitelist:velocity")

include("player-database")
include("player-database:api")
include("player-database:shared")
include("player-database:velocity")
include("player-database:paper")
include("player-database:proto")

include("options")
include("options:api")
include("options:shared")
include("options:paper")
include("options:velocity")
include("options:proto")

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "common"

// API 部分
include("common-library-member-api")
include("common-library-utils")
include("common-library-misc-api")
include("common-library-rpc-api")
include("common-library-exchange-api")

// 功能 & 实现部分
include("common-member")
include("common-dependency-loader")
include("common-misc")
include("common-utils")
include("common-dependency-loader-velocity")
include("common-hypervisor")
include("common-rpc")
include("common-bedrock-adaptive")
include("common-exchange")
pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "common"
include("common-member")
include("common-library-member-api")
include("common-dependency-loader")
include("common-misc")
include("common-library-utils")
include("common-library-misc-api")
include("common-utils")
include("common-dependency-loader-velocity")
include("common-transfer")
include("common-afk")
include("common-teleport")
include("common-bot")
include("common-chat")
include("common-hypervisor")

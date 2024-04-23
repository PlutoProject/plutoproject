dependencies {
    compileOnly("com.comphenix.protocol:ProtocolLib:5.1.0")
    compileOnly("dev.simplix:protocolize-api:2.3.3")
    compileOnly(project(":common-library-utils"))
    compileOnly(project(":common-library-member-api"))
    compileOnly("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")
    compileOnly("com.velocitypowered:velocity:3.3.0-379") // from Nostal's maven repo, used to handling packets
    kapt("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")
}
subprojects {
    dependencies {
        compileOnly(project(":utils:api"))
        compileOnly(project(":rpc:api"))
        compileOnly(project(":member:api"))
    }
}

tasks.shadowJar {
    relocate("ink.pmc.utils.proto", "ink.pmc.exchange.proto.utils")
}
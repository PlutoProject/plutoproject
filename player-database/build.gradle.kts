group = "ink.pmc.playerdb"

subprojects {
    dependencies {
        compileOnly(project(":utils:api"))
        compileOnly(project(":provider:api"))
        compileOnly(project(":rpc:api"))
    }
}
subprojects {
    dependencies {
        compileOnly(project(":utils:api"))
        compileOnly(project(":rpc:api"))
    }
}
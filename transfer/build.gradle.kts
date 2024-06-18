subprojects {
    dependencies {
        compileOnly(project(":utils:api"))
        compileOnly(project(":rpc:api"))
        compileOnly(project(":provider:api"))
    }
}
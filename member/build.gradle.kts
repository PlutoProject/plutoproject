subprojects {
    dependencies {
        compileOnly(project(":utils:api"))
        compileOnly(project(":provider:api"))
        compileOnly(project(":rpc:api"))
        compileOnly(rootProject.libs.floodgate)
    }
}
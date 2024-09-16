subprojects {
    dependencies {
        compileOnly(project(":utils:api"))
        compileOnly(project(":provider:api"))
    }
}
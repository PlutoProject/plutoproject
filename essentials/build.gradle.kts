subprojects {
    dependencies {
        compileOnly(project(":utils:api"))
        compileOnly(project(":provider:api"))
        compileOnly(project(":member:api"))
        compileOnly(project(":interactive:api"))
    }
}
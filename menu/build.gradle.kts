subprojects {
    dependencies {
        compileOnly(project(":utils:api"))
        compileOnly(project(":interactive:api"))
        compileOnly(project(":essentials:paper"))
    }
}
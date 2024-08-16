subprojects {
    dependencies {
        compileOnly(project(":utils:api"))
        compileOnly(project(":interactive:api"))
        compileOnly(project(":essentials:api"))
        compileOnly(project(":essentials:paper"))
        compileOnly(rootProject.libs.coreprotect)
    }
}
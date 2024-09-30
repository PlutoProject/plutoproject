subprojects {
    dependencies {
        compileOnly(project(":utils:api"))
        compileOnly(project(":interactive:api"))
        compileOnly(project(":essentials:api"))
        compileOnly(project(":essentials:paper"))
        compileOnly(project(":daily:paper"))
        compileOnly(project(":daily:api"))
        compileOnly(project(":player-database:api"))
        compileOnly(rootProject.libs.coreprotect)
    }
}
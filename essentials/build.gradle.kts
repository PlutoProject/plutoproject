subprojects {
    dependencies {
        compileOnly(project(":utils:api"))
        compileOnly(project(":provider:api"))
        compileOnly(project(":player-database:api"))
        compileOnly(project(":interactive:api"))
        compileOnly(rootProject.libs.huskHomes)
    }
}
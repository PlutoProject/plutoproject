subprojects {
    dependencies {
        compileOnly(rootProject.libs.vault.api)
        compileOnly(project(":utils:api"))
        compileOnly(project(":provider:api"))
        compileOnly(project(":member:api"))
    }
}
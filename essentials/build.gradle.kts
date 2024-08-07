subprojects {
    dependencies {
        compileOnly(rootProject.libs.vault.api) {
            isTransitive = false
        }
        compileOnly(project(":utils:api"))
        compileOnly(project(":provider:api"))
        compileOnly(project(":member:api"))
        compileOnly(project(":interactive:api"))
    }
}
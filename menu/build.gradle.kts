subprojects {
    dependencies {
        compileOnly(project(":framework:api"))
        compileOnly(project(":essentials:api"))
        compileOnly(project(":essentials:paper"))
        compileOnly(project(":daily:paper"))
        compileOnly(project(":daily:api"))
        compileOnly(project(":hypervisor:api"))
        compileOnly(rootProject.libs.coreprotect)
    }
}
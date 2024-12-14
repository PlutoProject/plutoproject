subprojects {
    dependencies {
        compileOnly(project(":framework:api"))
        compileOnly(project(":essentials:paper"))
    }
}
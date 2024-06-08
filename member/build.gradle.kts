subprojects {
    dependencies {
        compileOnly(project(":utils:api").apply { println(group) })
    }
}
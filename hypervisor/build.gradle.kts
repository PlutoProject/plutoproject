subprojects {
    dependencies {
        compileOnly(project(":utils:api"))
        compileOnly(project(":options:api"))
        compileOnly(parent!!.libs.spark.api)
    }
}
subprojects {
    dependencies {
        compileOnly(project(":utils:api"))
        compileOnly(parent!!.libs.spark.api)
    }
}
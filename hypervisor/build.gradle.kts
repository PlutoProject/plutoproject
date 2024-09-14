subprojects {
    dependencies {
        compileOnly(project(":framework:api"))
        compileOnly(parent!!.libs.spark.api)
    }
}
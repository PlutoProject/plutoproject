dependencies {
    compileOnly(project(":common-library-utils"))
    kapt(rootProject.libs.velocity.api)
}

tasks.shadowJar {
    mergeServiceFiles()
}
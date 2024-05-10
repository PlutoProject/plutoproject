dependencies {
    compileOnly(project(":common-library-utils"))
    compileOnly(project(":common-library-member-api"))
    compileOnly(libs.protocollib)
    compileOnly(libs.protocolize)
    compileOnly(libs.velocity)
    kapt(rootProject.libs.velocity.api)
}
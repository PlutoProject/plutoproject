dependencies {
    compileOnly(project(":common-library-utils"))
    compileOnly(project(":common-library-member-api"))
    kapt(libs.velocity.api)
}
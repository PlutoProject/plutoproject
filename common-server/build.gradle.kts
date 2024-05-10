dependencies {
    compileOnly(project(":common-library-utils"))
    implementation(project(":common-library-server-api"))
    kapt(libs.velocity.api)
}
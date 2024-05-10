dependencies {
    compileOnly(project(":common-library-utils"))
    implementation(project(":common-library-rpc-api"))
    kapt(libs.velocity.api)
}
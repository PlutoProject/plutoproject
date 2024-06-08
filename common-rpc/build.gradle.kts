dependencies {
    compileOnly(project(":utils"))
    implementation(project(":common-library-rpc-api"))
    kapt(libs.velocity.api)
}
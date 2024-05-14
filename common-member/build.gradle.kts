dependencies {
    implementation(project(":common-library-member-api"))
    compileOnly(project(":common-library-utils"))
    compileOnly(project(":common-library-rpc-api"))
    kapt(libs.velocity.api)
}

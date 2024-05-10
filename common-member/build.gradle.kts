dependencies {
    implementation(project(":common-library-member-api"))
    compileOnly(project(":common-library-utils"))
    kapt(libs.velocity.api)
}

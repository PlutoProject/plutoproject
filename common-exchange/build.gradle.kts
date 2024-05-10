dependencies {
    implementation(project(":common-library-exchange-api"))
    compileOnly(project(":common-library-utils"))
    compileOnly(project(":common-library-member-api"))
    kapt(rootProject.libs.velocity.api)
}
dependencies {
    implementation(project(":common-library-exchange-api"))
    compileOnly(project(":common-library-utils"))
    protobuf(project(":common-library-utils"))
    compileOnly(project(":common-library-member-api"))
    compileOnly(project(":common-library-rpc-api"))
    kapt(rootProject.libs.velocity.api)
}
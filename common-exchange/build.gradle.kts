dependencies {
    implementation(project(":common-library-exchange-api"))
    compileOnly(project(":common-library-utils"))
    protobuf(files("../proto"))
    compileOnly(project(":common-library-member-api"))
    compileOnly(project(":common-library-rpc-api"))
    kapt(rootProject.libs.velocity.api)
}
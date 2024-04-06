dependencies {
    implementation(project(":common-library-member-api"))
    compileOnly(project(":common-library-utils"))
    compileOnly("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")
    kapt("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")
    compileOnly("net.coreprotect:coreprotect:22.3")
}

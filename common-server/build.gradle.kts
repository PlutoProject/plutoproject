dependencies {
    implementation(project(":common-library-server-api"))
    compileOnly(project(":common-library-utils"))
    compileOnly("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")
    kapt("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")
}
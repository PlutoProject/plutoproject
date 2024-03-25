dependencies {
    implementation(project(":common-library-member-api"))
    compileOnly(project(":common-library-utils"))
    compileOnly("com.velocitypowered:velocity-api:4.0.0-SNAPSHOT")
    kapt("com.velocitypowered:velocity-api:4.0.0-SNAPSHOT")
}

dependencies {
    implementation(project(":common-library-exchange-api"))
    compileOnly(project(":common-library-utils"))
    compileOnly(project(":common-library-member-api"))
    compileOnly("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")
    kapt("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")
}
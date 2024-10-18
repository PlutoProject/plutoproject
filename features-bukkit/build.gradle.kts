plugins {
    alias(libs.plugins.paperweight.userdev)
}

dependencies {
    implementation(project(":common"))
    paperweight.paperDevBundle("1.21-R0.1-SNAPSHOT")
}
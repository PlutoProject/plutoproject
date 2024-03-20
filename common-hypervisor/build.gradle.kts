repositories {
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

dependencies {
    compileOnly(project(":common-library-utils"))
    compileOnly("me.lucko:spark-api:0.1-SNAPSHOT")
}
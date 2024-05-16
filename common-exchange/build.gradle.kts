dependencies {
    implementation(project(":common-library-exchange-api"))
    compileOnly(project(":common-library-utils"))
    protobuf(project(":common-library-utils"))
    compileOnly(project(":common-library-member-api"))
    kapt(rootProject.libs.velocity.api)
}

/*
sourceSets {
    main {
        proto {
            srcDir(files("../common-library-utils/src/main/proto"))
        }
    }
}*/

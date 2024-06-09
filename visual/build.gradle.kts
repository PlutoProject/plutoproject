subprojects {
    dependencies {
        compileOnly(project(":utils:api"))
        implementation(parent!!.libs.ultimate.advancement)
    }
}

tasks.shadowJar {
    relocate("com.fren_gor.ultimateAdvancementAPI", "ink.pmc.visual.libs.ultimateadvancementapi")
}
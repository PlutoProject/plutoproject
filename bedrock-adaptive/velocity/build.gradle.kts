dependencies {
    compileOnly(parent!!.libs.protocolize)
}

velocityPluginJson {
    dependency("member")
    dependency("protocolize")
    dependency("geyser")
    dependency("floodgate")
}
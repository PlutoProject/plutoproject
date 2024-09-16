dependencies {
    compileOnly(parent!!.libs.protocolize)
}

velocityPluginJson {
    dependency("protocolize")
    dependency("geyser")
    dependency("floodgate")
}
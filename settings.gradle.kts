pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
include (":unityLibrary")
project(":unityLibrary").projectDir = File("fenixAndroid\\unityLibrary")
include (":unityLibrary:xrmanifest.androidlib")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        flatDir {
            dirs ("${project(":unityLibrary").projectDir}/libs")
        }
    }
}

rootProject.name = "ARventurePath"
include(":app")


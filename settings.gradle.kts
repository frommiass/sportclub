pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "karateclub"
include(":app")
include(":core")
include(":data")
include(":domain")
include(":features:players")
include(":features:groups")
include(":ui-common")
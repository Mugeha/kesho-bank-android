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

rootProject.name = "kesho-bank-android"
include(":app")
include(":companion-poc-apps:malicious-second-app")
include(":companion-poc-apps:tapjacking-overlay")
project(":companion-poc-apps:malicious-second-app").projectDir =
    file("companion-poc-apps/malicious-second-app")
project(":companion-poc-apps:tapjacking-overlay").projectDir =
    file("companion-poc-apps/tapjacking-overlay")

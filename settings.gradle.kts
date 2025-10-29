pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "SharkoGuess"

include(
    ":app",
    ":core:designsystem",
    ":core:common",
    ":core:network",
    ":core:database",
    ":core:player",
    ":data:applemusic",
    ":data:firebase",
    ":domain",
    ":feature:onboarding",
    ":feature:artistpicker",
    ":feature:category",
    ":feature:solo",
    ":feature:versus",
    ":feature:leaderboard",
    ":feature:settings"
)

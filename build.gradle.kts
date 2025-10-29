plugins {   
    application
}

version = "v1.0.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

application {   
    mainClass = "game.Main"
}

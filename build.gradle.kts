plugins {   
    application
}

version = "1.0.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

application {   
    mainClass = "fluffy.Main"
}

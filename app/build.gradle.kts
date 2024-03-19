plugins {
    id("java")
    id("application")
    id("org.openjfx.javafxplugin") version "0.0.13"
}

group = "org.se13"
version = "0.1-alpha"

repositories {
    mavenCentral()
}

javafx {
    modules("javafx.controls", "javafx.fxml")
}


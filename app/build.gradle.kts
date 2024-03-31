plugins {
    id("java")
    id("application")
    id("org.openjfx.javafxplugin") version "0.0.13"
    id("org.beryx.jlink") version "3.0.1"
}

group = "org.se13"
version = "0.1-alpha"

application {
    mainClass = "org.se13.SE13Application"
    mainModule = "org.se13"
}

repositories {
    mavenCentral()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

javafx {
    modules("javafx.controls", "javafx.fxml")
}

dependencies {
    // https://mvnrepository.com/artifact/org.openjfx/javafx-controls
    implementation("org.openjfx:javafx-controls:21.0.2")

    // https://mvnrepository.com/artifact/org.openjfx/javafx-fxml
    implementation("org.openjfx:javafx-fxml:21.0.2")

    implementation("org.xerial:sqlite-jdbc:3.44.1.0")
    implementation("org.json:json:20240303")
}
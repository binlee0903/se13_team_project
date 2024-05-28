plugins {
    id("java")
    id("application")
    id("org.openjfx.javafxplugin") version "0.0.13"
    id("org.beryx.jlink") version "3.0.1"
}

group = "org.se13"
version = "1.0"

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>{
    options.encoding = "UTF-8"
}

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

tasks.withType<Test> {
    useJUnitPlatform()
}

javafx {
    modules("javafx.controls", "javafx.fxml")
}

jlink {
    options = listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages")
    launcher {
        name = "tetris"
    }
    jpackage {
        imageOptions = listOf("--icon", "src/main/resources/favicon.ico")
    }
    forceMerge("sqlite")
}

dependencies {
    // https://mvnrepository.com/artifact/org.openjfx/javafx-controls
    implementation("org.openjfx:javafx-controls:21.0.2")

    // https://mvnrepository.com/artifact/org.openjfx/javafx-fxml
    implementation("org.openjfx:javafx-fxml:21.0.2")

    implementation("org.xerial:sqlite-jdbc:3.44.1.0")
    implementation("org.json:json:20240303")

    implementation("com.google.code.gson:gson:2.11.0")

    implementation("org.slf4j:slf4j-api:2.0.6")
    implementation("ch.qos.logback:logback-classic:1.4.14")

    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
    testImplementation("org.assertj:assertj-core:3.23.1")
}
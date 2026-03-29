plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.avocado"
version = "1.0.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

val dropwizardVersion = "4.0.7"

dependencies {
    implementation("io.dropwizard:dropwizard-core:$dropwizardVersion")

    testImplementation("io.dropwizard:dropwizard-testing:$dropwizardVersion")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

application {
    mainClass = "com.avocado.api.AvocadoApiApplication"
}

tasks.test {
    useJUnitPlatform()
}

tasks.shadowJar {
    archiveBaseName = "avocado"
    archiveClassifier = ""
    mergeServiceFiles()
}

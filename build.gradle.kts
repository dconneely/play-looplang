plugins {
    java
    application
    id("com.diffplug.spotless") version "8.2.1"
}

group = "com.davidconneely"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

application {
    mainClass = "com.davidconneely.looplang.Main"
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.0.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    archiveBaseName = "looplang"
    manifest {
        attributes("Main-Class" to "com.davidconneely.looplang.Main")
    }
}

spotless {
    java {
        googleJavaFormat()
    }
}

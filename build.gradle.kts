import java.io.ByteArrayOutputStream
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    java
    idea
    id("com.gorylenko.gradle-git-properties") version "2.2.2"
    id("io.freefair.lombok") version "5.1.0"

    // jfx
    application
    id("org.openjfx.javafxplugin") version "0.0.8"

    id("org.beryx.jlink") version "2.19.0"
}

repositories {
    mavenCentral()
}
dependencies {
    testImplementation("junit", "junit", "4.11")
}

gitProperties {
    keys = listOf(
            "git.branch",
            "git.closest.tag.commit.count",
            "git.closest.tag.name",
            "git.commit.id",
            "git.commit.id.abbrev",
            "git.commit.id.describe",
            "git.commit.time"
    )
    val buildTime = OffsetDateTime.now().truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    customProperty("build.time", buildTime)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

javafx {
    version = "14.0.1"
    modules = listOf("javafx.controls", "javafx.fxml")
}

tasks.jar {
    val imgPath = "net/teatwig/mineswraft/images"
    exclude(
            "${imgPath}/icon.svg",
            "${imgPath}/icon64.ico"
    )
}

// build:build creates a zip with all required dependencies in "build/distributions"
application {
    mainModule.set("net.teatwig.mineswraft")
    mainClass.set("net.teatwig.mineswraft.MainFX")
}

/*
 * Gets the version name from the latest Git tag
 */
fun gitDescribeVersion(): String {
    val stdout = ByteArrayOutputStream()
    project.exec {
        commandLine("git", "describe", "--dirty")
        standardOutput = stdout
        isIgnoreExitValue = true
    }
    val version = stdout.toString().trim()
    return if (version.isEmpty()) {
        "DEV"
    } else {
        version.replaceFirst("^v", "");
    }
}
val projectNameWithVersion = "${rootProject.name}-${gitDescribeVersion()}"

distributions {
    main {
        distributionBaseName.set("${projectNameWithVersion}-java")
    }
}
tasks.withType<Tar> {
    compression = Compression.GZIP
    archiveExtension.set("tar.gz")
}

// build:jlink/jlinkZip creates a zip containing everything and a JRE, so it should be able to run on systems that don"t have java installed
jlink {
    addOptions("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages")
    launcher {
        name = rootProject.name
    }
    imageName.set("${projectNameWithVersion}-rt")
}

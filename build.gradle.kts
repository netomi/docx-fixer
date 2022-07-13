import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.21"
    application
}

group   = "com.github.netomi"
version = "1.0"

application {
    mainClass.set("com.github.netomi.doc.DocxFixerCommand")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("info.picocli:picocli:4.6.3")

    implementation("xmlpull:xmlpull:1.1.3.1")
    implementation("net.sf.kxml:kxml2:2.3.0")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
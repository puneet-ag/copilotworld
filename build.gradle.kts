plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.intellij")
}

group = "com.dpworld"
version = "1.0-SNAPSHOT"

fun environment(key: String) = providers.environmentVariable(key)

repositories {
    mavenCentral()
    gradlePluginPortal()
}

intellij {
    version.set("2023.3.6")
    type.set("IC")
    plugins.set(listOf("java"))
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("233")
        untilBuild.set("241.*")
    }

//    signPlugin {
//        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
//        privateKey.set(System.getenv("PRIVATE_KEY"))
//        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
//    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}

dependencies {
    implementation(platform(libs.jackson.bom))
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation(libs.flexmark.all) {
        // vulnerable transitive dependency
        exclude(group = "org.jsoup", module = "jsoup")
    }

//    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:okhttp:4.9.0")
    implementation("com.squareup.okhttp3:okhttp-sse:4.9.0")
    implementation(libs.jsoup)
    implementation(libs.commons.text)
    implementation(libs.tree.sitter)
    implementation(libs.jtokkit)
    implementation("io.github.bonede:tree-sitter-java:0.21.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    testImplementation(kotlin("test"))
}

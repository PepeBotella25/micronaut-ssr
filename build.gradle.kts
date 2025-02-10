plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.micronaut.application") version "4.4.4"
    id("io.micronaut.aot") version "4.4.4"
}

version = "0.1"
group = "com.poc.micronaut.ssr"

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor("io.micronaut:micronaut-http-validation")
    annotationProcessor("io.micronaut.serde:micronaut-serde-processor")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    compileOnly("io.micronaut:micronaut-http-client")
    runtimeOnly("ch.qos.logback:logback-classic")
    testImplementation("io.micronaut:micronaut-http-client")

    implementation("io.micronaut.views:micronaut-views-react")
    implementation("org.graalvm.polyglot:polyglot:24.1.1")
    implementation("org.graalvm.polyglot:js:24.1.1")
    implementation("io.projectreactor:reactor-tools:3.6.10")
    implementation("io.micronaut:micronaut-http-client")
}


application {
    mainClass = "com.poc.micronaut.ssr.Application"
}
java {
    sourceCompatibility = JavaVersion.toVersion("17")
    targetCompatibility = JavaVersion.toVersion("17")
}


graalvmNative.toolchainDetection = false

micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("com.poc.micronaut.ssr.*")
    }
    aot {
        // Please review carefully the optimizations enabled below
        // Check https://micronaut-projects.github.io/micronaut-aot/latest/guide/ for more details
        optimizeServiceLoading = false
        convertYamlToJava = false
        precomputeOperations = true
        cacheEnvironment = true
        optimizeClassLoading = true
        deduceEnvironment = true
        optimizeNetty = true
        replaceLogbackXml = true
    }
}

fun isWindows(): Boolean {
    return System.getProperty("os.name", "").lowercase().contains("windows")
}

fun getNpmCmd(): String {
    val command = if (isWindows()) "npm.cmd" else "npm"
    return command
}

tasks.register<Exec>("buildFrontend") {
    workingDir = file("./src/frontend")
    commandLine = listOf(getNpmCmd(), "run", "build")
}

tasks.compileJava {
    dependsOn("buildFrontend")
}


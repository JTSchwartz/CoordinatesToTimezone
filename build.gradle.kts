import java.lang.invoke.MethodHandles.invoker

val invoker by configurations.creating

plugins {
    kotlin("jvm") version "1.5.31"
    id("com.github.johnrengelman.shadow") version "6.0.0"
    application
}

group = "com.jtschwartz"
version = "1.0.0"

val name: String by rootProject
val mainClass = "$group.coordinatesToTimezone.$name"

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.github.microutils:kotlin-logging:2.1.16")
    implementation("com.google.cloud.functions:functions-framework-api:1.0.4")
    invoker("com.google.cloud.functions.invoker:java-function-invoker:1.0.3")
    
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    testImplementation("org.mockito:mockito-core:4.1.0")
    testImplementation("com.google.truth:truth:1.1.3")
    testImplementation("com.google.guava:guava-testlib:31.0.1-jre")
}

application {
    mainClassName = "${mainClass}Kt"
}

task<JavaExec>("runFunction") {
    main = "com.google.cloud.functions.invoker.runner.Invoker"
    classpath(invoker)
    inputs.files(configurations.runtimeClasspath, sourceSets["main"].output)
    args(
        "--target", project.findProperty("runFunction.target") ?: mainClass,
        "--port", project.findProperty("runFunction.port") ?: 8080
        )
    doFirst {
        args("--classpath", files(configurations.runtimeClasspath, sourceSets["main"].output).asPath)
    }
}

tasks.named("build") {
    dependsOn(":shadowJar")
}

task("buildFunction") {
    dependsOn("build")
    copy {
        from("build/libs/" + rootProject.name + "-all.jar")
        into("build/deploy")
    }
}
plugins {
    java
    jacoco
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.spring") version "1.6.10"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.springframework.boot") version "2.6.3"
}

repositories {
    mavenCentral()
}

kotlin {
    target {
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
                apiVersion = "1.6"
                languageVersion = "1.6"
            }
        }
    }
}
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

val integrationTest: SourceSet by sourceSets.creating {
    java.srcDir("src/integration-test/java")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("stdlib-jdk7"))
    implementation(kotlin("stdlib-jdk8"))
    
    implementation("org.apache.maven:maven-artifact:3.8.4")
    implementation("org.springframework.boot:spring-boot-starter-webflux") {
        exclude("org.springframework.boot", "spring-boot-starter-tomcat")
    }
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.codehaus.janino:janino")
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")
    
    "integrationTestImplementation"(project)
    "integrationTestImplementation"("org.junit.jupiter:junit-jupiter-api")
    "integrationTestImplementation"("io.github.bonigarcia:selenium-jupiter:3.3.0")
    "integrationTestImplementation"("com.codeborne:selenide:5.2.8")
    "integrationTestImplementation"("io.percy:percy-java-selenium:1.0.0")
    "integrationTestImplementation"("org.springframework.boot:spring-boot-starter-test") {
        exclude("junit", "junit")
        exclude("org.junit.jupiter")
        exclude("org.junit.vintage")
    }
    "integrationTestRuntimeOnly"("org.junit.jupiter:junit-jupiter-engine")
    
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude("junit", "junit")
        exclude("org.junit.jupiter")
        exclude("org.junit.vintage")
    }
    testImplementation("io.projectreactor:reactor-test")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

val jacocoTestReport = tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        html.required.set(true)
        xml.required.set(true)
    }
}

tasks {
    val integrationTestTask by register<Test>("integrationTest") {
        description = "Runs integration tests."
        group = "verification"
        
        useJUnitPlatform()
        testClassesDirs = integrationTest.output.classesDirs
        classpath = integrationTest.runtimeClasspath
        
        mustRunAfter(test)
    }
    
    check {
        dependsOn(jacocoTestReport)
        dependsOn(integrationTestTask)
    }
    test {
        useJUnitPlatform()
        finalizedBy(jacocoTestReport)
    }
    withType<AbstractArchiveTask> {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }
}
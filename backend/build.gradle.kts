plugins {
    id("application")
    kotlin("jvm") version Versions.kotlin
    kotlin("plugin.serialization") version Versions.kotlin
}

val mainClassName = "de.maaxgr.ddltoktorm.backend.MainKt"

group = "de.maaxgr"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation("io.insert-koin:koin-core:${Versions.koin}")
    implementation("io.insert-koin:koin-test:${Versions.koin}")

    // ktor
    implementation("io.ktor:ktor-server-netty:${Versions.ktor}")
    implementation("ch.qos.logback:logback-classic:${Versions.logback}")
    testImplementation("io.ktor:ktor-server-tests:${Versions.ktor}")
    implementation("io.ktor:ktor-gson:${Versions.ktor}")
    implementation("io.ktor:ktor-auth:${Versions.ktor}")

    // config
    implementation("com.charleskorn.kaml:kaml:${Versions.kaml}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinXSerializationJson}")

    // database
    implementation("mysql:mysql-connector-java:${Versions.mysqlConnector}")
    implementation("org.ktorm:ktorm-core:${Versions.ktorm}")
    implementation("org.ktorm:ktorm-support-mysql:${Versions.ktorm}")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("com.github.seratch:kotliquery:1.7.0")

    // bcrypt hashing
    implementation("org.springframework.security:spring-security-core:5.6.3")

    // fuel http
    implementation("com.github.kittinunf.fuel:fuel:2.3.1")

}

tasks.named<JavaExec>("run") {
    mainClass.set(mainClassName)
    workingDir = File("${projectDir.absolutePath}/run")
}

tasks.withType<Jar>() {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes("Main-Class" to mainClassName)
    }

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}

plugins {
    kotlin("jvm") version "1.3.72"
}


repositories {
    mavenCentral()
    jcenter()
}

val ktor_version = "1.2.6"

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("io.ktor:ktor-server-netty:$ktor_version")

    implementation("com.graphql-java:graphql-java:13.0")
    implementation("com.github.excitement-engineer:ktor-graphql:1.0.0")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.9.1")

    testImplementation("com.google.code.gson:gson:2.8.0")
    testImplementation("junit:junit:4.11")
    testImplementation("io.ktor:ktor-server-test-host:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test")

}
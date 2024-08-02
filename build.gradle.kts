plugins {
    id("java")
}

group = "org.apio3"
version = "0.2.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.jsoup:jsoup:1.17.1")
}

tasks.test {
    useJUnitPlatform()
}
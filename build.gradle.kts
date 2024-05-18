plugins {
    id("java")
}

group = "org.apio3"
version = "0.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    // jsoup HTML parser library @ https://jsoup.org/
    implementation("org.jsoup:jsoup:1.17.1")
}

tasks.test {
    useJUnitPlatform()
}
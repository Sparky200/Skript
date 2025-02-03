plugins {
	id("java")
}

group = "com.github.SkriptLang"

repositories {
	mavenCentral()
}

dependencies {
	testImplementation(platform("org.junit:junit-bom:5.10.0"))
	testImplementation("org.junit.jupiter:junit-jupiter")

	implementation("com.google.guava:guava:32.1.2-jre")
	implementation("org.jetbrains:annotations:26.0.1")

	implementation(project(":skript-api"))

}

java {
}

tasks.compileJava {
	options.encoding = "UTF-8"
}

tasks.compileTestJava {
	options.encoding = "UTF-8"
}

tasks.test {
	useJUnitPlatform()
}

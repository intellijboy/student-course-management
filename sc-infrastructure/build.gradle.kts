plugins {
    id("java-library")
}

group = "cn.intellijboy"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencies {
    api(project(":sc-domain"))
    api(project(":sc-common"))

    implementation(platform("org.springframework.boot:spring-boot-dependencies:4.1.2"))

    // Spring 容器/依赖注入注解（@Component/@Repository/@Configuration 等）
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework:spring-context")
}
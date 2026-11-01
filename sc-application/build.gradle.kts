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

    // 统一 Spring Boot 依赖版本（与 sc-main 一致），各层模块以 BOM 管理版本号
    implementation(platform("org.springframework.boot:spring-boot-dependencies:4.1.2"))

    // 应用服务用到的 Spring 注解（@Service/@Configuration/@Transactional 等）
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
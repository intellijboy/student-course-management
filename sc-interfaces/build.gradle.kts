plugins {
    id("java-library")
}

group = "cn.intellijboy"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

// web 控制器用 `@PathVariable`（未显式写参数名）依赖反射读取方法参数名，
// 需保留编译期参数名（-parameters），否则 Spring 无法绑定 @PathVariable → 400。
tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}

dependencies {
    api(project(":sc-application"))
    api(project(":sc-domain"))
    api(project(":sc-infrastructure"))
    api(project(":sc-common"))

    implementation(platform("org.springframework.boot:spring-boot-dependencies:4.1.2"))

    // Spring Web MVC：控制器注解、请求/响应绑定
    implementation("org.springframework.boot:spring-boot-starter-web")
}
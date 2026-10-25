plugins {
    java
    id("org.springframework.boot") version "4.1.2"
    id("io.spring.dependency-management") version "1.1.7"
    id("io.github.javapackager") version "2.0.0"
}

group = "cn.intellijboy"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

javapackager {
    // Spring Boot 胖 jar 为嵌套结构（BOOT-INF/），无法用 `-cp <jar> 应用主类` 启动；
    // JavaPackager 生成的启动命令（launch4j exe / 平台脚本）均以 -cp 方式启动。
    // 因此启动入口采用 fat jar 外层的 JarLauncher，由其从 MANIFEST 的 Start-Class 加载应用。
    mainClass("org.springframework.boot.loader.launch.JarLauncher")
    // 便携分发包：自带全量运行时（不裁剪 jdeps 模块，避免反射/ServiceLoader 缺模块）
    bundleJre(true)
    customizedJre(false)
    // 非目标：不生成原生安装器（exe/msi/deb/rpm/dmg）
    generateInstaller(false)
    // 平台压缩包：Windows 产 zip，Linux/macOS 产 tar.gz
    createZipball(true)
    createTarball(true)
    // 以 Spring Boot 胖 jar 作为分发包内唯一 jar，不复制依赖，避免重复与体积膨胀
    runnableJar(layout.buildDirectory.file("libs/${project.name}-${project.version}.jar").get().asFile)
    copyDependencies(false)
    // copyDependencies=false 时插件不会自动生成类路径，需显式指定分发包内的 bootJar
    classpath("${project.name}-${project.version}.jar")
    // Windows：jar 以独立文件形式随 exe 分发（不嵌入 exe），满足"应用 jar + 启动命令 + jre/"结构
    winConfig.setWrapJar(false)
    // Linux：同理，启动脚本与 jar 分离，分发包内保留独立 bootJar
    linuxConfig.setWrapJar(false)
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

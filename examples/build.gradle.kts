plugins {
    id("application")
    id("scala")
    id("com.gradleup.shadow") version "9.3.1"
}

application {
    mainClass = "com.systemvi.examples.Main"
}

val currentOs = org.gradle.internal.os.OperatingSystem.current()
if (currentOs.isMacOsX) {
    application {
        applicationDefaultJvmArgs = listOf("-XstartOnFirstThread")
    }
}


val run: JavaExec by tasks
run.standardInput = System.`in`

sourceSets {
    main {
        scala {
            setSrcDirs(listOf("src/scala"))
        }
        resources {
            setSrcDirs(listOf("src/resources"))
        }
    }
}

tasks.register("compile-release") {
    val systemVIVersion: String by project
    subprojects.forEach {
        dependsOn(it.tasks.named("jar"))
    }
    rootProject.subprojects.forEach {
        if (it.name == "engine") dependsOn(it.tasks.named("shadowJar"))
    }
    doLast {
        val exclusions= listOf("cats","firmata","test","collections","java-and-scala","jni-lib","multi-window","parallel","tetris","warriors")
//        val currentDir = file(".")
        val buildDir = file("build")

        if (buildDir.exists()) {
            buildDir.delete()
        }
        buildDir.mkdir()

        val engineJar = file("../engine/build/libs/engine-$systemVIVersion-all.jar")

        engineJar.copyTo(file("build/engine.jar"), true)

        subprojects.forEach {
            if(it.name !in exclusions) {
                val subprojectDir = file("build/${it.name}")
                val subprojectAssetsDir = file("${it.name}/assets")
                val subprojectJar = file("${it.name}/build/libs/${it.name}.jar")
                val launcherFile = file("build/${it.name}/launcher.bat")

                subprojectDir.mkdir()
                if (subprojectAssetsDir.exists())
                    subprojectAssetsDir.copyRecursively(file("build/${it.name}/assets"), true)
                subprojectJar.copyTo(file("build/${it.name}/lib.jar"), true)

                launcherFile.createNewFile()
                val writer = launcherFile.writer()
                writer.write("java -cp \"lib.jar;../engine.jar\" ${it.application.mainClass.get()}")
                writer.flush()
                writer.close()
                launcherFile.setExecutable(true)
            }
        }
    }
}

tasks.register("create-subproject"){

}

repositories {
    mavenCentral()
    maven {
        setUrl("https://oss.sonatype.org/content/repositories/snapshots/")
    }
}

val listOfImplementations: DependencyHandlerScope.() -> Unit = {
    val currentOs = org.gradle.internal.os.OperatingSystem.current()

    val lwjglVersion: String by project
    val jomlVersion: String by project
    project.ext.set("lwjglVersion", lwjglVersion)
    project.ext.set("jomlVersion", jomlVersion)

    if (currentOs.isLinux) {
        val osArch = System.getProperty("os.arch")
        val isArm = osArch.startsWith("arm") || osArch.startsWith("aarch64")
        val isArm8or64 = osArch.contains("64") || osArch.startsWith("armv8")
        project.ext.set(
            "lwjglNatives",
            if (isArm) "natives-linux-${if (isArm8or64) "arm64" else "arm32"}" else "natives-linux"
        )
    }
    if (currentOs.isMacOsX) {
        val osArch = System.getProperty("os.arch")
        val isArm = osArch.startsWith("arm") || osArch.startsWith("aarch64")
        project.ext.set("lwjglNatives", if (isArm) "natives-macos-arm64" else "natives-macos")
    }
    if (currentOs.isWindows) {
        val osArch = System.getProperty("os.arch")
        val isArm = osArch.contains("64")
        val isAarch64 = osArch.startsWith("aarch64")
        project.ext.set(
            "lwjglNatives",
            if (isArm) "natives-windows${if (isAarch64) "-arm64" else ""}" else "natives-windows-x86"
        )
    }
    val lwjglNatives = project.ext.get("lwjglNatives")



    implementation(project(":engine"))

    implementation("dev.dominion.ecs:dominion-ecs-engine:0.9.0")

    implementation("com.google.code.gson:gson:2.10.1")

    implementation("org.jbox2d:jbox2d-library:2.2.1.1")

    implementation("com.googlecode.lanterna:lanterna:3.0.1")

    implementation("org.scala-lang:scala3-library_3:3.5.1")

    implementation("commons-collections:commons-collections:3.2.2")

    implementation("org.joml:joml:${jomlVersion}")

    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))

    implementation("org.typelevel:cats-effect_3:3.6-0142603")
    implementation("org.typelevel:cats-core_3:2.12.0")

    implementation("org.lwjgl:lwjgl")
    implementation("org.lwjgl:lwjgl-assimp")
    implementation("org.lwjgl:lwjgl-bgfx")
    implementation("org.lwjgl:lwjgl-cuda")
    implementation("org.lwjgl:lwjgl-egl")
    implementation("org.lwjgl:lwjgl-fmod")
    implementation("org.lwjgl:lwjgl-freetype")
    implementation("org.lwjgl:lwjgl-glfw")
    implementation("org.lwjgl:lwjgl-harfbuzz")
    implementation("org.lwjgl:lwjgl-hwloc")
    implementation("org.lwjgl:lwjgl-jawt")
    implementation("org.lwjgl:lwjgl-jemalloc")
    implementation("org.lwjgl:lwjgl-ktx")
    implementation("org.lwjgl:lwjgl-libdivide")
    implementation("org.lwjgl:lwjgl-llvm")
    implementation("org.lwjgl:lwjgl-lmdb")
    implementation("org.lwjgl:lwjgl-lz4")
    implementation("org.lwjgl:lwjgl-meow")
    implementation("org.lwjgl:lwjgl-meshoptimizer")
    implementation("org.lwjgl:lwjgl-nanovg")
    implementation("org.lwjgl:lwjgl-nfd")
    implementation("org.lwjgl:lwjgl-nuklear")
    implementation("org.lwjgl:lwjgl-odbc")
    implementation("org.lwjgl:lwjgl-openal")
    implementation("org.lwjgl:lwjgl-opencl")
    implementation("org.lwjgl:lwjgl-opengl")
    implementation("org.lwjgl:lwjgl-opengles")
    implementation("org.lwjgl:lwjgl-opus")
    implementation("org.lwjgl:lwjgl-par")
    implementation("org.lwjgl:lwjgl-remotery")
    implementation("org.lwjgl:lwjgl-rpmalloc")
    implementation("org.lwjgl:lwjgl-shaderc")
    implementation("org.lwjgl:lwjgl-spvc")
    implementation("org.lwjgl:lwjgl-stb")
    implementation("org.lwjgl:lwjgl-tinyexr")
    implementation("org.lwjgl:lwjgl-tinyfd")
    implementation("org.lwjgl:lwjgl-vma")
    implementation("org.lwjgl:lwjgl-vulkan")
    implementation("org.lwjgl:lwjgl-xxhash")
    implementation("org.lwjgl:lwjgl-yoga")
    implementation("org.lwjgl:lwjgl-zstd")
    runtimeOnly("org.lwjgl:lwjgl::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-assimp::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-bgfx::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-freetype::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-glfw::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-harfbuzz::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-hwloc::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-jemalloc::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-ktx::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-libdivide::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-llvm::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-lmdb::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-lz4::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-meow::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-meshoptimizer::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-nanovg::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-nfd::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-nuklear::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-openal::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-opengl::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-opengles::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-opus::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-par::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-remotery::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-rpmalloc::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-shaderc::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-spvc::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-stb::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-tinyexr::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-tinyfd::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-vma::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-xxhash::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-yoga::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-zstd::$lwjglNatives")

}

dependencies(listOfImplementations)

subprojects {
    apply(plugin = "application")
    apply(plugin = "scala")
    apply(plugin = "com.github.johnrengelman.shadow")

    val currentOs = org.gradle.internal.os.OperatingSystem.current()
    if (currentOs.isMacOsX) {
        application {
            applicationDefaultJvmArgs = listOf("-XstartOnFirstThread")
        }
    }

    repositories {
        mavenCentral()
        maven {
            setUrl("https://oss.sonatype.org/content/repositories/snapshots/")
        }
    }

    dependencies(listOfImplementations)
}
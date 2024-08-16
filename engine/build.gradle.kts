plugins {
    id("application")
    id("scala")
    id("com.github.johnrengelman.shadow") version("8.1.1")
    id("maven-publish")
    id("java-library")
}

group="com.systempro.systemvi"
version="0.7.3"

java{
    withJavadocJar()
    withSourcesJar()
}

scala {
    zincVersion = "1.6.1"
}

application {
    mainClass = "com.systemvi.Main"
}

val run: JavaExec by tasks
run.standardInput = System.`in`

private val currentOs=org.gradle.internal.os.OperatingSystem.current()
private val linux=org.gradle.internal.os.OperatingSystem.LINUX
private val windows=org.gradle.internal.os.OperatingSystem.WINDOWS
private val macos=org.gradle.internal.os.OperatingSystem.MAC_OS

val lwjglVersion:String by project
val jomlVersion:String by project
project.ext.set("lwjglVersion",lwjglVersion)
project.ext.set("jomlVersion",jomlVersion)

when (currentOs) {
    linux->{
        val osArch = System.getProperty("os.arch")
        val isArm=osArch.startsWith("arm")||osArch.startsWith("aarch64")
        val isArm8or64=osArch.contains("64") || osArch.startsWith("armv8")
        project.ext.set("lwjglNatives",if(isArm) "natives-linux-${if(isArm8or64) "arm64" else "arm32"}" else "natives-linux")
    }
    macos->{
        val osArch = System.getProperty("os.arch")
        val isArm=osArch.startsWith("arm")||osArch.startsWith("aarch64")
        project.ext.set("lwjglNatives",if(isArm)"natives-macos-arm64" else "natives-macos")
    }
    windows->{
        val osArch = System.getProperty("os.arch")
        val isArm=osArch.contains("64")
        val isAarch64=osArch.startsWith("aarch64")
        project.ext.set("lwjglNatives",if(isArm) "natives-windows${if(isAarch64)"-arm64" else ""}" else "natives-windows-x86")
    }
}
if(currentOs==macos){
    application{
        applicationDefaultJvmArgs = listOf("-XstartOnFirstThread")
    }
}

println(project.ext.get("lwjglVersion"))
println(project.ext.get("jomlVersion"))
println(project.ext.get("lwjglNatives"))
println(currentOs.name)
println(currentOs.version)
println(currentOs.path)
println(currentOs.isWindows)
//
//repositories {
//    mavenCentral()
//    maven {
//        url "https://oss.sonatype.org/content/repositories/snapshots/"
//    }
//}
//
//sourceSets {
//    main {
//        scala {
//            srcDirs += ['src/scala']
//        }
//        resources {
//            srcDirs+=['src/resources']
//        }
//    }
//}
//
////tasks.withType(ScalaCompile) {
////	scalaCompileOptions.forkOptions.with {
////		memoryMaximumSize = '1g'
////		jvmArgs = ['-XX:MaxMetaspaceSize=512m']
////	}
////}
//
//dependencies {
//    implementation 'dev.dominion.ecs:dominion-ecs-engine:0.9.0'
//    implementation 'com.google.code.gson:gson:2.10.1'
//    implementation 'org.jbox2d:jbox2d-library:2.2.1.1'
//    // https://mvnrepository.com/artifact/com.googlecode.lanterna/lanterna
//    implementation group: 'com.googlecode.lanterna', name: 'lanterna', version: '3.0.1'
//
//    implementation 'org.scala-lang:scala-library:2.11.12'
//
//    implementation "org.joml:joml:${jomlVersion}"
//
//    implementation platform("org.lwjgl:lwjgl-bom:$lwjglVersion")
//
//    implementation "org.lwjgl:lwjgl"
//    implementation "org.lwjgl:lwjgl-assimp"
//    implementation "org.lwjgl:lwjgl-bgfx"
//    implementation "org.lwjgl:lwjgl-cuda"
//    implementation "org.lwjgl:lwjgl-egl"
//    implementation "org.lwjgl:lwjgl-fmod"
//    implementation "org.lwjgl:lwjgl-freetype"
//    implementation "org.lwjgl:lwjgl-glfw"
//    implementation "org.lwjgl:lwjgl-harfbuzz"
//    implementation "org.lwjgl:lwjgl-hwloc"
//    implementation "org.lwjgl:lwjgl-jawt"
//    implementation "org.lwjgl:lwjgl-jemalloc"
//    implementation "org.lwjgl:lwjgl-ktx"
//    implementation "org.lwjgl:lwjgl-libdivide"
//    implementation "org.lwjgl:lwjgl-llvm"
//    implementation "org.lwjgl:lwjgl-lmdb"
//    implementation "org.lwjgl:lwjgl-lz4"
//    implementation "org.lwjgl:lwjgl-meow"
//    implementation "org.lwjgl:lwjgl-meshoptimizer"
//    implementation "org.lwjgl:lwjgl-nanovg"
//    implementation "org.lwjgl:lwjgl-nfd"
//    implementation "org.lwjgl:lwjgl-nuklear"
//    implementation "org.lwjgl:lwjgl-odbc"
//    implementation "org.lwjgl:lwjgl-openal"
//    implementation "org.lwjgl:lwjgl-opencl"
//    implementation "org.lwjgl:lwjgl-opengl"
//    implementation "org.lwjgl:lwjgl-opengles"
//    implementation "org.lwjgl:lwjgl-openvr"
//    implementation "org.lwjgl:lwjgl-opus"
//    implementation "org.lwjgl:lwjgl-par"
//    implementation "org.lwjgl:lwjgl-remotery"
//    implementation "org.lwjgl:lwjgl-rpmalloc"
//    implementation "org.lwjgl:lwjgl-shaderc"
//    implementation "org.lwjgl:lwjgl-spvc"
//    implementation "org.lwjgl:lwjgl-sse"
//    implementation "org.lwjgl:lwjgl-stb"
//    implementation "org.lwjgl:lwjgl-tinyexr"
//    implementation "org.lwjgl:lwjgl-tinyfd"
//    implementation "org.lwjgl:lwjgl-tootle"
//    implementation "org.lwjgl:lwjgl-vma"
//    implementation "org.lwjgl:lwjgl-vulkan"
//    implementation "org.lwjgl:lwjgl-xxhash"
//    implementation "org.lwjgl:lwjgl-yoga"
//    implementation "org.lwjgl:lwjgl-zstd"
//    runtimeOnly "org.lwjgl:lwjgl::$lwjglNatives"
//    runtimeOnly "org.lwjgl:lwjgl-assimp::$lwjglNatives"
//    runtimeOnly "org.lwjgl:lwjgl-bgfx::$lwjglNatives"
//    runtimeOnly "org.lwjgl:lwjgl-freetype::$lwjglNatives"
//    runtimeOnly "org.lwjgl:lwjgl-glfw::$lwjglNatives"
//    runtimeOnly "org.lwjgl:lwjgl-harfbuzz::$lwjglNatives"
//    runtimeOnly "org.lwjgl:lwjgl-hwloc::$lwjglNatives"
//    runtimeOnly "org.lwjgl:lwjgl-jemalloc::$lwjglNatives"
//    runtimeOnly "org.lwjgl:lwjgl-ktx::$lwjglNatives"
//    runtimeOnly "org.lwjgl:lwjgl-libdivide::$lwjglNatives"
//    runtimeOnly "org.lwjgl:lwjgl-llvm::$lwjglNatives"
//    runtimeOnly "org.lwjgl:lwjgl-lmdb::$lwjglNatives"
//    runtimeOnly "org.lwjgl:lwjgl-lz4::$lwjglNatives"
//    runtimeOnly "org.lwjgl:lwjgl-meow::$lwjglNatives"
//    runtimeOnly "org.lwjgl:lwjgl-meshoptimizer::$lwjglNatives"
//    runtimeOnly "org.lwjgl:lwjgl-nanovg::$lwjglNatives"
//    runtimeOnly "org.lwjgl:lwjgl-nfd::$lwjglNatives"
//    runtimeOnly "org.lwjgl:lwjgl-nuklear::$lwjglNatives"
//    runtimeOnly "org.lwjgl:lwjgl-openal::$lwjglNatives"
//    runtimeOnly "org.lwjgl:lwjgl-opengl::$lwjglNatives"
//    runtimeOnly "org.lwjgl:lwjgl-opengles::$lwjglNatives"
//    runtimeOnly "org.lwjgl:lwjgl-openvr::$lwjglNatives"
//    runtimeOnly "org.lwjgl:lwjgl-opus::$lwjglNatives"
//    runtimeOnly "org.lwjgl:lwjgl-par::$lwjglNatives"
//    runtimeOnly "org.lwjgl:lwjgl-remotery::$lwjglNatives"
//    runtimeOnly "org.lwjgl:lwjgl-rpmalloc::$lwjglNatives"
//    runtimeOnly "org.lwjgl:lwjgl-shaderc::$lwjglNatives"
//    runtimeOnly "org.lwjgl:lwjgl-spvc::$lwjglNatives"
//    runtimeOnly "org.lwjgl:lwjgl-sse::$lwjglNatives"
//    runtimeOnly "org.lwjgl:lwjgl-stb::$lwjglNatives"
//    runtimeOnly "org.lwjgl:lwjgl-tinyexr::$lwjglNatives"
//    runtimeOnly "org.lwjgl:lwjgl-tinyfd::$lwjglNatives"
//    runtimeOnly "org.lwjgl:lwjgl-tootle::$lwjglNatives"
//    runtimeOnly "org.lwjgl:lwjgl-vma::$lwjglNatives"
//    runtimeOnly "org.lwjgl:lwjgl-xxhash::$lwjglNatives"
//    runtimeOnly "org.lwjgl:lwjgl-yoga::$lwjglNatives"
//    runtimeOnly "org.lwjgl:lwjgl-zstd::$lwjglNatives"
//}
//
//subprojects {
//    apply plugin: 'java'
//
//    repositories {
//        mavenCentral()
//        maven {
//            url "https://oss.sonatype.org/content/repositories/snapshots/"
//        }
//    }
//    dependencies {
//        implementation rootProject
//                implementation 'dev.dominion.ecs:dominion-ecs-engine:0.9.0'
//        implementation 'com.google.code.gson:gson:2.10.1'
//        implementation 'org.jbox2d:jbox2d-library:2.2.1.1'
//        // https://mvnrepository.com/artifact/com.googlecode.lanterna/lanterna
//        implementation group: 'com.googlecode.lanterna', name: 'lanterna', version: '3.0.1'
//
//        implementation 'org.scala-lang:scala-library:2.11.12'
//
//        implementation "org.joml:joml:${jomlVersion}"
//
//        implementation platform("org.lwjgl:lwjgl-bom:$lwjglVersion")
//
//        implementation "org.lwjgl:lwjgl"
//        implementation "org.lwjgl:lwjgl-assimp"
//        implementation "org.lwjgl:lwjgl-bgfx"
//        implementation "org.lwjgl:lwjgl-cuda"
//        implementation "org.lwjgl:lwjgl-egl"
//        implementation "org.lwjgl:lwjgl-fmod"
//        implementation "org.lwjgl:lwjgl-freetype"
//        implementation "org.lwjgl:lwjgl-glfw"
//        implementation "org.lwjgl:lwjgl-harfbuzz"
//        implementation "org.lwjgl:lwjgl-hwloc"
//        implementation "org.lwjgl:lwjgl-jawt"
//        implementation "org.lwjgl:lwjgl-jemalloc"
//        implementation "org.lwjgl:lwjgl-ktx"
//        implementation "org.lwjgl:lwjgl-libdivide"
//        implementation "org.lwjgl:lwjgl-llvm"
//        implementation "org.lwjgl:lwjgl-lmdb"
//        implementation "org.lwjgl:lwjgl-lz4"
//        implementation "org.lwjgl:lwjgl-meow"
//        implementation "org.lwjgl:lwjgl-meshoptimizer"
//        implementation "org.lwjgl:lwjgl-nanovg"
//        implementation "org.lwjgl:lwjgl-nfd"
//        implementation "org.lwjgl:lwjgl-nuklear"
//        implementation "org.lwjgl:lwjgl-odbc"
//        implementation "org.lwjgl:lwjgl-openal"
//        implementation "org.lwjgl:lwjgl-opencl"
//        implementation "org.lwjgl:lwjgl-opengl"
//        implementation "org.lwjgl:lwjgl-opengles"
//        implementation "org.lwjgl:lwjgl-openvr"
//        implementation "org.lwjgl:lwjgl-opus"
//        implementation "org.lwjgl:lwjgl-par"
//        implementation "org.lwjgl:lwjgl-remotery"
//        implementation "org.lwjgl:lwjgl-rpmalloc"
//        implementation "org.lwjgl:lwjgl-shaderc"
//        implementation "org.lwjgl:lwjgl-spvc"
//        implementation "org.lwjgl:lwjgl-sse"
//        implementation "org.lwjgl:lwjgl-stb"
//        implementation "org.lwjgl:lwjgl-tinyexr"
//        implementation "org.lwjgl:lwjgl-tinyfd"
//        implementation "org.lwjgl:lwjgl-tootle"
//        implementation "org.lwjgl:lwjgl-vma"
//        implementation "org.lwjgl:lwjgl-vulkan"
//        implementation "org.lwjgl:lwjgl-xxhash"
//        implementation "org.lwjgl:lwjgl-yoga"
//        implementation "org.lwjgl:lwjgl-zstd"
//        runtimeOnly "org.lwjgl:lwjgl::$lwjglNatives"
//        runtimeOnly "org.lwjgl:lwjgl-assimp::$lwjglNatives"
//        runtimeOnly "org.lwjgl:lwjgl-bgfx::$lwjglNatives"
//        runtimeOnly "org.lwjgl:lwjgl-freetype::$lwjglNatives"
//        runtimeOnly "org.lwjgl:lwjgl-glfw::$lwjglNatives"
//        runtimeOnly "org.lwjgl:lwjgl-harfbuzz::$lwjglNatives"
//        runtimeOnly "org.lwjgl:lwjgl-hwloc::$lwjglNatives"
//        runtimeOnly "org.lwjgl:lwjgl-jemalloc::$lwjglNatives"
//        runtimeOnly "org.lwjgl:lwjgl-ktx::$lwjglNatives"
//        runtimeOnly "org.lwjgl:lwjgl-libdivide::$lwjglNatives"
//        runtimeOnly "org.lwjgl:lwjgl-llvm::$lwjglNatives"
//        runtimeOnly "org.lwjgl:lwjgl-lmdb::$lwjglNatives"
//        runtimeOnly "org.lwjgl:lwjgl-lz4::$lwjglNatives"
//        runtimeOnly "org.lwjgl:lwjgl-meow::$lwjglNatives"
//        runtimeOnly "org.lwjgl:lwjgl-meshoptimizer::$lwjglNatives"
//        runtimeOnly "org.lwjgl:lwjgl-nanovg::$lwjglNatives"
//        runtimeOnly "org.lwjgl:lwjgl-nfd::$lwjglNatives"
//        runtimeOnly "org.lwjgl:lwjgl-nuklear::$lwjglNatives"
//        runtimeOnly "org.lwjgl:lwjgl-openal::$lwjglNatives"
//        runtimeOnly "org.lwjgl:lwjgl-opengl::$lwjglNatives"
//        runtimeOnly "org.lwjgl:lwjgl-opengles::$lwjglNatives"
//        runtimeOnly "org.lwjgl:lwjgl-openvr::$lwjglNatives"
//        runtimeOnly "org.lwjgl:lwjgl-opus::$lwjglNatives"
//        runtimeOnly "org.lwjgl:lwjgl-par::$lwjglNatives"
//        runtimeOnly "org.lwjgl:lwjgl-remotery::$lwjglNatives"
//        runtimeOnly "org.lwjgl:lwjgl-rpmalloc::$lwjglNatives"
//        runtimeOnly "org.lwjgl:lwjgl-shaderc::$lwjglNatives"
//        runtimeOnly "org.lwjgl:lwjgl-spvc::$lwjglNatives"
//        runtimeOnly "org.lwjgl:lwjgl-sse::$lwjglNatives"
//        runtimeOnly "org.lwjgl:lwjgl-stb::$lwjglNatives"
//        runtimeOnly "org.lwjgl:lwjgl-tinyexr::$lwjglNatives"
//        runtimeOnly "org.lwjgl:lwjgl-tinyfd::$lwjglNatives"
//        runtimeOnly "org.lwjgl:lwjgl-tootle::$lwjglNatives"
//        runtimeOnly "org.lwjgl:lwjgl-vma::$lwjglNatives"
//        runtimeOnly "org.lwjgl:lwjgl-xxhash::$lwjglNatives"
//        runtimeOnly "org.lwjgl:lwjgl-yoga::$lwjglNatives"
//        runtimeOnly "org.lwjgl:lwjgl-zstd::$lwjglNatives"
//    }
//}
//
//
//
//abstract class PrintOSName extends DefaultTask {
//    @TaskAction
//    def lol() {
//        println(osName())
//    }
//    static def osName(){
//        switch (OperatingSystem.current()){
//            case OperatingSystem.WINDOWS:return 'windows'
//            case OperatingSystem.LINUX:return 'linux'
//            case OperatingSystem.MAC_OS:return 'macos'
//        }
//    }
//}
//
//// Create a task using the task type
//tasks.register('os-name', PrintOSName)
//
//
//
//publishing {
//    repositories {
//        maven {
//            name = "System_VI_Engine"
//            url = uri("https://maven.pkg.github.com/UrosSysPro/systemVI")
//            credentials {
//                username = System.getenv("GITHUB_USERNAME")
//                password = System.getenv("GITHUB_API_KEY")
//            }
//        }
//    }
//    publications {
////		shadow(MavenPublication) { publication ->
////			artifactId 'system-vi'
////			project.shadow.component(publication)
////		}
//        shadow(MavenPublication) { publication ->
//            artifactId 'system-vi-'+PrintOSName.osName()
//            project.shadow.component(publication)
//        }
//    }
//}
//
//javadoc {
//    if(JavaVersion.current().isJava9Compatible()) {
//        options.addBooleanOption('html5', true)
//    }
//}

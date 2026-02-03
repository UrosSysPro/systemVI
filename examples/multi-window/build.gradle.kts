plugins {
    id("application")
    id("scala")
id("com.gradleup.shadow") version "9.3.1"}
scala {
    zincVersion = "1.6.1"
}
application {
    mainClass = "com.systemvi.multi_window.Main"
}

val run: JavaExec by tasks
run.standardInput = System.`in`

sourceSets {
    main {
        scala {
            setSrcDirs(listOf("src/scala"))
        }
        resources{
            setSrcDirs(listOf("src/resources"))
        }
    }
}

dependencies {
//    implementation project(":jni-lib")
}
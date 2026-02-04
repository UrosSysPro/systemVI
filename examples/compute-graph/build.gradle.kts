plugins {
    id("application")
    id("scala")
    id("com.gradleup.shadow") version "9.3.1"
}
application {
    mainClass = "com.systemvi.graph.Main"
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
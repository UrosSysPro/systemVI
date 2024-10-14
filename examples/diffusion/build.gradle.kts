plugins {
    id("application")
    id("scala")
    id("com.github.johnrengelman.shadow") version("8.1.1")
}
scala {
    zincVersion = "1.6.1"
}

val startClassName:String="com.systemvi.diffusion.Main"

application {
    mainClass = startClassName
}

val run: JavaExec by tasks
run.standardInput = System.`in`

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = startClassName
    }
}

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
plugins {
    id("application")
    id("scala")
    id("com.github.johnrengelman.shadow") version("8.1.1")
}
scala {
    zincVersion = "1.6.1"
}
application {
    mainClass = "com.systemvi.models.Main"
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
    implementation("org.typelevel:cats-effect_3:3.6-0142603")
    implementation("org.typelevel:cats-core_3:2.12.0")
}
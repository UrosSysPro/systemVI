plugins {
    id("application")
    id("scala")
    id("com.github.johnrengelman.shadow") version("8.1.1")
}
scala {
    zincVersion = "1.6.1"
}
application {
    mainClass = "com.systemvi.firmata.Main"
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
    implementation("com.github.kurbatov:firmata4j:2.3.8")
    implementation("org.slf4j:slf4j-simple:2.1.0-alpha1")
}
plugins {
    id("application")
    id("scala")
    id("com.github.johnrengelman.shadow") version("8.1.1")
}
scala {
    zincVersion = "1.6.1"
}
application {
    mainClass = "com.systemvi.cats.game.Main"
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

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.http4s:http4s-ember-client_3:1.0.0-M44")
    implementation("org.http4s:http4s-ember-server_3:1.0.0-M44")
    implementation("org.http4s:http4s-dsl_3:1.0.0-M44")
    implementation("org.typelevel:log4cats-slf4j_3:2.7.0")
    implementation("org.slf4j:slf4j-simple:2.0.16")
}
plugins {
  id("scala")
  id("application")
  id("com.github.johnrengelman.shadow") version("8.1.1")
}

scala {
  zincVersion = "1.6.1"
}
application {
  mainClass = "com.systemvi.jni.Main"
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

//compileJava {
//  options.compilerArgs += ["-h", file("src/main/include")]
//}
//
//task compileJNI {
//  dependsOn compileJava
//
//  doLast {
//    exec {
//      commandLine 'sh', '-c', 'mkdir -p build/natives && cd build/natives && cmake ../.. && make'
//    }
//  }
//}
//
//clean.doFirst {
//  delete fileTree('src/main/include') {
//    include 'jni_*.h'
//  }
//}
//
//processResources {
//  dependsOn compileJNI
//}
//
//tasks.withType(Test) {
//  systemProperty "java.library.path", "build/natives/lib"
//  testLogging.showStandardStreams = false
//}
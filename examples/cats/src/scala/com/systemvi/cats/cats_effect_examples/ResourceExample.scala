package com.systemvi.cats.cats_effect_examples

import cats.effect.*
import cats.effect.implicits.*
import cats.*
import cats.implicits.*

import java.io.{File, FileWriter}
import java.util.Scanner

object ResourceExample extends IOApp.Simple {
  override def run: IO[Unit] = for {
    lines <- resources.use {
      case (scanner, writter) => for {
        lines <- IO{
          var lines=0
          while(scanner.hasNextLine){
            writter.write(s"${scanner.nextLine()}\n")
            lines+=1
          }
          lines
        }
      } yield lines
    }.attempt
    _<-IO{
      lines match
        case Left(e)=>
          println("error")
          e.printStackTrace()
        case Right(value)=>println(value)
    }
  } yield ()

  def inputResource: Resource[IO, Scanner] = Resource.make {
    IO {
      Scanner(File("examples/cats/build.gradle.kts"))
    }
  } { scanner =>
    IO {
      scanner.close()
    }
  }

  def outputResource: Resource[IO, FileWriter] = Resource.make {
    IO {
      FileWriter(File("examples/cats/output.txt"))
    }
  } { writer =>
    IO {
      writer.close()
    }
  }

  def resources = (inputResource, outputResource).tupled
}

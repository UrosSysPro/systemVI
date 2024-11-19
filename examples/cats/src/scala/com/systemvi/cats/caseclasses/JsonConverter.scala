package com.systemvi.cats.caseclasses

enum Json:
  case JsonNumber() extends Json
  case JsonString() extends Json
  case JsonObject() extends Json

object JsonConverter {
  def main(args: Array[String]): Unit = {

  }
}
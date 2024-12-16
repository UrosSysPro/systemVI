package com.systemvi.cats.game

import cats.effect.*
import cats.effect.implicits.*
import cats.*
import cats.implicits.*

abstract class IOGame {

  def setup:IO[Unit]
  def loop:IO[Unit]
}

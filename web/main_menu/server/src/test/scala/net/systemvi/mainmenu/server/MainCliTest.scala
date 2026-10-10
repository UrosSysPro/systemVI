package net.systemvi.mainmenu.server

import com.monovore.decline.Command
import munit.FunSuite

class MainCliTest extends FunSuite {

  private val command = Command("main-menu", "test header")(Main.main)

  test("no arguments falls back to serving") {
    assert(command.parse(Nil).isRight)
  }

  test("serve is accepted") {
    assert(command.parse(List("serve")).isRight)
  }

  test("migrate is accepted") {
    assert(command.parse(List("migrate")).isRight)
  }

  test("migrate --force is accepted") {
    assert(command.parse(List("migrate", "--force")).isRight)
  }

  test("migrate down is accepted") {
    assert(command.parse(List("migrate", "down")).isRight)
  }

  test("seed is accepted") {
    assert(command.parse(List("seed")).isRight)
  }

  test("config is accepted") {
    assert(command.parse(List("config")).isRight)
  }

  test("an unknown subcommand is rejected") {
    assert(command.parse(List("bogus")).isLeft)
  }
}

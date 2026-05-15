package net.systemvi.server.persistance.migrations

trait Migration[F[_]] {

  def up: F[Int]

  def down: F[Int]
}

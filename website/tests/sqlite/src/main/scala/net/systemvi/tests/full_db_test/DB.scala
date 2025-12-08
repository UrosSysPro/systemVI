package net.systemvi.tests.full_db_test

case class DB[F[_]](manufacturer:ManufacturerDB[F],switch: SwitchDB[F],keyboard: KeyboardDB[F])
package net.systemvi.tests.full_db_test

import cats.effect.{IO, IOApp}

import java.util.UUID

object Main extends IOApp.Simple {

  override def run: IO[Unit] = sqlite.use { sqlite =>
    val db = DB(
      ManufacturerDB.create(sqlite),
      SwitchDB.create(sqlite),
      KeyboardDB.create(sqlite),
    )

    val keychron = Manufacturer(UUID.randomUUID(), "Keychron")
    val kailh = Manufacturer(UUID.randomUUID(), "Kailh")
    val gateron = Manufacturer(UUID.randomUUID(), "Gateron")
    val otemu = Manufacturer(UUID.randomUUID(), "Otemu")

    val kProBlue = Switch(UUID.randomUUID(), "K Pro Blue", keychron.uuid, Clicky)
    val kProMint = Switch(UUID.randomUUID(), "K Pro Mint", keychron.uuid, Tactile)
    val kProSilver = Switch(UUID.randomUUID(), "K Pro Silver", keychron.uuid, Linear)
    val phantomRed = Switch(UUID.randomUUID(), "Phantom Red", gateron.uuid, Linear)

    val boxSilver = Switch(UUID.randomUUID(), "Box Silver", keychron.uuid, Linear)

    val otemuBrown = Switch(UUID.randomUUID(), "Otemu Brown", gateron.uuid, Tactile)
    val otemuPurple = Switch(UUID.randomUUID(), "Otemu Purple", gateron.uuid, Linear)
    val otemuYellow = Switch(UUID.randomUUID(), "Otemu Yellow", gateron.uuid, Linear)

    val ph60Marble = Keyboard(UUID.randomUUID(),"PH 60 Marble Pla",kProMint.uuid)
    val ph60MetallicViolet = Keyboard(UUID.randomUUID(),"PH 60 Metallic Violet Pla",boxSilver.uuid)

    for {
      _ <- IO.println("hello")
      _ <- List(db.manufacturer.dropTable(),db.switch.dropTable(),db.keyboard.dropTable()).sequence
      _ <- List(db.manufacturer.createTable(),db.switch.createTable(),db.keyboard.createTable()).sequence
      _ <- List(keychron,kailh,gateron,otemu).map(db.manufacturer.add).sequence
      _ <- List(kProBlue,kProMint,kProSilver,phantomRed,boxSilver,otemuBrown,otemuPurple,otemuYellow).map(db.switch.add).sequence
      _ <- List(ph60Marble,ph60MetallicViolet).map(db.keyboard.add).sequence
      keyboards <- db.keyboard.get()
      _ <- keyboards.map(_.name).map(IO.println).sequence
    } yield ()
  }
}

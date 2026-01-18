package net.systemvi.server.persistance.seeders

import cats.effect.*
import cats.effect.implicits.*
import cats.*
import cats.implicits.*
import cats.effect.kernel.MonadCancelThrow
import doobie.util.transactor.Transactor
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import net.systemvi.server.persistance.models.*
import net.systemvi.server.persistance.contexts.{ManufacturerContext, *}

import java.util.UUID

object KeyboardSeeders {
  def seed(xa:Transactor[IO]): IO[Unit] = {
    val manufacturers = ManufacturerContext.create(xa)
    val switches = SwitchContext.create(xa)
    val keyboards = KeyboardContext.create(xa)
    val images = EntityImageContext.create(xa)

    val keychron =  Manufacturer(UUID.randomUUID(), "Keychron")
    val kailh =     Manufacturer(UUID.randomUUID(), "Kailh")
    val gateron =   Manufacturer(UUID.randomUUID(), "Gateron")
    val otemu =     Manufacturer(UUID.randomUUID(), "Otemu")

    val kProBlue =    Switch(UUID.randomUUID(),  keychron.uuid, Clicky.id,  "K Pro Blue")
    val kProMint =    Switch(UUID.randomUUID(),  keychron.uuid, Tactile.id, "K Pro Mint")
    val kProSilver =  Switch(UUID.randomUUID(),  keychron.uuid, Linear.id,  "K Pro Silver")
    val phantomRed =  Switch(UUID.randomUUID(),  gateron.uuid,  Linear.id,  "Phantom Red")
    val boxSilver =   Switch(UUID.randomUUID(),  kailh.uuid,    Linear.id,  "Box Silver")
    val otemuBrown =  Switch(UUID.randomUUID(),  gateron.uuid,  Tactile.id, "Otemu Brown")
    val otemuPurple = Switch(UUID.randomUUID(),  gateron.uuid,  Linear.id,  "Otemu Purple")
    val otemuYellow = Switch(UUID.randomUUID(),  gateron.uuid,  Linear.id,  "Otemu Yellow")

    val ph60MetallicViolet =  Keyboard(UUID.randomUUID(),boxSilver.uuid,  Profile60.id,"PH 60 Metallic Violet Pla", "ph_60_metallic_violet_pla")
    val corneWireless =       Keyboard(UUID.randomUUID(),phantomRed.uuid,  ProfileCorne.id,"Corne Wireless", "corne_wireless")
    val cornePrototype =      Keyboard(UUID.randomUUID(),otemuYellow.uuid,  ProfileCorne.id,"Corne Prototype", "corne_prototype")
    val ph60Marble =          Keyboard(UUID.randomUUID(),kProMint.uuid,   Profile60.id,"PH 60 Marble Pla",          "ph_60_marble_pla")
    val bana40 =              Keyboard(UUID.randomUUID(),phantomRed.uuid,   Profile40.id,"Bana 40%",          "bana_40")
    val tklRabbit =           Keyboard(UUID.randomUUID(),kProMint.uuid,   ProfileTKL.id,"TKL Rabbit",          "tkl_rabbit")
    val dactyl =              Keyboard(UUID.randomUUID(),otemuBrown.uuid,   ProfileDactyl.id,"7x5 Dactyl",          "dactyl")

    val corneWirelessImages = List(
      EntityImage(corneWireless.uuid,"/images/corne-wireless.jpg",0)
    )
    val cornePrototypeImages = List(
      EntityImage(cornePrototype.uuid,"/images/corne-prototype.jpg",0)
    )
    val ph60marbleImages = List(
      EntityImage(ph60Marble.uuid,"/images/keyboard-60.jpg",0)
    )
    val ph60MetallicVioletImages = List(
      EntityImage(ph60MetallicViolet.uuid,"/images/keyboard-60.jpg",0)
    )
    val bana40Images = List(
      EntityImage(bana40.uuid,"/images/red-keyboard.jpg",0)
    )
    val tklRabbitImages = List(
      EntityImage(tklRabbit.uuid,"/images/tkl-rabbit.jpg",0)
    )
    val dactylImages = List(
      EntityImage(dactyl.uuid,"/images/dactyl2.jpg",0)
    )

    for{
      _<-List(keychron,kailh,gateron,otemu).map(a=>manufacturers.add(a)).sequence
      _<-List(kProBlue,kProMint,kProSilver,phantomRed,boxSilver,otemuBrown,otemuPurple,otemuYellow).map(a=>switches.add(a)).sequence
      _<-List(
        ph60Marble,
        ph60MetallicViolet,
        corneWireless,
        cornePrototype,
        bana40,
        tklRabbit,
        dactyl,
      ).map(a=>keyboards.add(a)).sequence
      _<-List(
        corneWirelessImages,
        cornePrototypeImages,
        ph60marbleImages,
        ph60MetallicVioletImages,
        bana40Images,
        tklRabbitImages,
        dactylImages,
      ).flatten.map(a=>images.add(a)).sequence
    } yield ()
  }
}

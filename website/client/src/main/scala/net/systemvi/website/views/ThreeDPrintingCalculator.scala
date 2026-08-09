package net.systemvi.website.views

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.common.dtos.*
import cats.*
import cats.implicits.*
import io.circe.scalajs.*
import io.circe.generic.*
import io.circe.generic.auto.*
import net.systemvi.website.*
import net.systemvi.website.utils.*
import net.systemvi.website.darkproject.neo_navbar.*
import net.systemvi.website.darkproject.footer.*
import org.scalajs.dom

val printingCostPerHour = 50

case class HardwareItem(name: String, price: Int)

case class Filament(name: String, pricePerKg: Int)

case class Model(name: String, massInGrams: Int, printTime: Int)

case class PrintedItem(filament: Filament, model: Model)

sealed trait ThreeDPrintedProductComponent{
  def price: Int
}
case class HardwareComponent(item: HardwareItem, count: Int) extends ThreeDPrintedProductComponent{
  override def price: Int = item.price * count
}
case class PrintedComponent(item: PrintedItem, count: Int ) extends ThreeDPrintedProductComponent{
  override def price: Int =
    (item.filament.pricePerKg.toFloat * (item.model.massInGrams.toFloat/1000) * count +
    item.model.printTime.toFloat/60 * printingCostPerHour * count).toInt
}

case class ThreeDPrintedProduct(components: List[ThreeDPrintedProductComponent]){
  def totalPrice(): Int = {
    val total: Float = this.components.foldLeft(0f)(_+_.price)
    total.toInt
  }
}

def ThreeDPrintingCalculator() = {

  // Filaments
  val blue =            Filament("Devil Design Blue",           2600)
  val brown =           Filament("Creality Brown",              3000)

  val darkGreen =       Filament("Devil Design Dark Green",     2600)
  val yellow =          Filament("Creality Yellow",             2300)
  val lightGreen =      Filament("Creality Light Green",        1600)
  val gray =            Filament("Creality Gray",               1600)
  val orange =          Filament("Creality Orange",             1600)
  val purple =          Filament("Devil Design Purple",         1300)

  val black =           Filament("Creality Black",              1600)
  val white =           Filament("Creality White",              1600)
  val metalicPurple =   Filament("Devil Design Metalic Purple", 2400)

  // Hardware items
  val magnet =          HardwareItem("6x3 Magnet",              5)

  // Tiles
  val wood  =           Model("Wood",13, 40)
  val wheat =           Model("Wheat",13, 40)
  val sheep =           Model("Sheep",13, 40)
  val stone =           Model("Stone",13, 40)
  val brick =           Model("Brick",13, 40)
  val gold  =           Model("Gold",13, 40)

  val desert =          Model("Desert",13, 40)

  val water =           Model("Water", 9, 30)
  val waterBay =        Model("Water Bay", 10, 30)

  // Tile accessories
  val bayShip =         Model("Bay Ship", 4, 20)
  val bayTrade =        Model("Bay Trade", 4, 20)

  val frame =           Model("Frame", 10, 20)

  // Players
  val playerBox =       Model("Player Box", 100, 180)
  val playerPeices =    Model("Player Pieces", 100, 180)

  val playerShipBox =   Model("Player Ship Box", 100, 180)
  val playerShipPeices =Model("Player Ship Pieces", 100, 180)

  // Holder
  val tileHolder =      Model("Tile Holder", 120, 180)

  val baseGame = ThreeDPrintedProduct(
    List(
      // Tiles
      PrintedComponent(PrintedItem(darkGreen, wood), 4),
      PrintedComponent(PrintedItem(yellow, wheat), 4),
      PrintedComponent(PrintedItem(lightGreen, sheep), 4),
      PrintedComponent(PrintedItem(gray, stone), 3),
      PrintedComponent(PrintedItem(orange, brick), 3),
      PrintedComponent(PrintedItem(brown, desert), 1),
      PrintedComponent(PrintedItem(blue, water), 9),
      PrintedComponent(PrintedItem(blue, waterBay), 9),

      // Frames
      PrintedComponent(PrintedItem(black, frame), 37),

      HardwareComponent(magnet, 222),

      // Tile accessories
      PrintedComponent(PrintedItem(gray, bayShip),9),
      PrintedComponent(PrintedItem(brown, bayTrade),9),

      // Players
      PrintedComponent(PrintedItem(orange, playerBox),1),
      PrintedComponent(PrintedItem(orange, playerPeices),1),

      PrintedComponent(PrintedItem(blue, playerBox),1),
      PrintedComponent(PrintedItem(blue, playerPeices),1),

      PrintedComponent(PrintedItem(lightGreen, playerBox),1),
      PrintedComponent(PrintedItem(lightGreen, playerPeices),1),

      PrintedComponent(PrintedItem(white, playerBox),1),
      PrintedComponent(PrintedItem(white, playerPeices),1),
    )
  )

  div(
    display.flex, flexDirection.column, alignItems.center, paddingTop.rem(6),
    div(
      display.flex, flexDirection.column, justifyContent.start, width.percent(100), maxWidth.px(1450),
      NeoNavbar(),

      baseGame.components.map{
        case component: HardwareComponent =>
          div(
            display.flex, flexDirection.row, gap.rem(3),
            div(
              width.rem(40),
              component.item.name
            ),
            div(
              component.price
            )
          )
        case component: PrintedComponent =>
          div(
            display.flex, flexDirection.row, gap.rem(3),
            div(
              width.rem(40),
              display.flex, flexDirection.row, gap.rem(3),
              div(
                width.rem(20),
                s"${component.item.model.name}",
              ),
              div(
                s"${component.item.filament.name}"
              ),
            ),
            div(
              s"${component.price}"
            ),
          )
      },

      div(s"total price ${baseGame.totalPrice()}"),

      Footer(),
    )
  )
}

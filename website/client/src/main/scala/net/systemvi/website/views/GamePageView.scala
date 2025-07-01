package net.systemvi.website.views

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.website.GamePage
import net.systemvi.website.api.GameApi
import net.systemvi.website.darkproject.navbar.Navbar
import net.systemvi.website.darkproject.product_info.*
import net.systemvi.website.darkproject.product_info.given
import net.systemvi.website.darkproject.big_title.BigTitle
import net.systemvi.website.darkproject.expandable_specs.*
import net.systemvi.website.darkproject.bill_of_materials.*
import net.systemvi.website.darkproject.footer.*

def GamePageView(page:GamePage):HtmlElement={
  val game=GameApi.get(page.gameId)
  div(
    cls:="flex flex-col items-center pt-24",
    div(
      className:="flex flex-col justify-start w-full max-w-[1450px]",
      BigTitle(
        game.name,
      ),
      Navbar(),
      ProductInfo(game),
//      ImageSlider(game.images),
      BigTitle("Technical Specifications"),
      ExpandableSpecs(game.specs),
      BigTitle("Bill Of Materials"),
      BillOfMaterials(),
      Footer(),
    )
  )
}
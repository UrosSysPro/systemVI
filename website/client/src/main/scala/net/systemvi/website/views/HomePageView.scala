package net.systemvi.website.views

import com.raquo.laminar.api.L.{*, given}
import io.circe.scalajs.*
import io.circe.generic.auto.*
import net.systemvi.common.dtos.*
import net.systemvi.website.api.{EngineApi, GameApi}
import net.systemvi.website.darkproject.footer.Footer
import net.systemvi.website.darkproject.neo_navbar.*
import net.systemvi.website.darkproject.section.*
import net.systemvi.website.darkproject.slider.ImageSlider
import net.systemvi.website.*
import net.systemvi.website.routes.Pages.*
import org.scalajs.dom

def HomePageView():HtmlElement = {
  val keyboards = Var[List[KeyboardDto]](List.empty)
  val engine = EngineApi.get()
  val games = GameApi.all()

  dom.fetch(s"${Constants.serverUrl}/keyboards").`then`{ response =>
    response.json().`then`{ json =>
      val list = decodeJs[List[KeyboardDto]](json).getOrElse(List.empty)
      keyboards.writer.onNext(list)
    }
  }

  div(
    cls:="flex flex-col items-center pt-24",
    div(
      className:="flex flex-col justify-start w-full max-w-[1450px]",
      NeoNavbar(),
      ImageSlider(
        images = List(
          "images/keyboards-all.jpg",
          "images/keyboards-all-rotated.jpg",
        )
      ),
      child <-- keyboards.signal.map{ keyboards=>
        Section(
          "Keyboards",
          keyboards.take(4).map(k=>SectionItem(k.name,k.images.head.imageUrl,KeyboardPage(k.uuid))),
          KeyboardsPage
        )
      },
      Section(
        "Games",
        games.take(4).map(g=>SectionItem(g.name,g.images.head,GamePage(g.id))),
        GamesPage
      ),
      Section(
        "Engine Demo",
        engine.demos.take(4).map(e=>SectionItem(e.name,e.images.head,HomePage)),
        EnginePage
      ),
      AboutSection(),
      Footer(),
    )
  )
}
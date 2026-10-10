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
import net.systemvi.website.utils.Constants
import org.scalajs.dom

def HomePageView():HtmlElement = {
  val keyboards = Var[List[KeyboardDto]](List.empty)
  val engine = Var[List[ApplicationDto]](List.empty)
  val games = Var[List[ApplicationDto]](List.empty)

  dom.fetch(s"${Constants.serverUrl}/keyboards").`then`{ response =>
    response.json().`then`{ json =>
      val list = decodeJs[List[KeyboardDto]](json).getOrElse(List.empty)
      keyboards.writer.onNext(list)
    }
  }

  dom.fetch(s"${Constants.serverUrl}/applications/games").`then`{ response =>
    response.json().`then`{ json =>
      val list = decodeJs[List[ApplicationDto]](json).getOrElse(List.empty)
      games.writer.onNext(list)
    }
  }

  dom.fetch(s"${Constants.serverUrl}/applications/tech-demos").`then`{ response =>
    response.json().`then`{ json =>
      val list = decodeJs[List[ApplicationDto]](json).getOrElse(List.empty)
      engine.writer.onNext(list)
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
      child <-- games.signal.map{ games =>
        Section(
          "Games",
          games.take(4).map(g => SectionItem(g.name, g.images.head.imageUrl, ApplicationDetailsPage(g.uuid))),
          GamesPage
        )
      },
      child <-- engine.signal.map{ engine =>
        Section(
          "Engine Demo",
          engine.take(4).map(e => SectionItem(e.name, e.images.head.imageUrl, ApplicationDetailsPage(e.uuid))),
          EnginePage
        )
      },
      AboutSection(),
      Footer(),
    )
  )
}
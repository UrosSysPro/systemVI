package net.systemvi.website.views

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.common.dtos.KeyboardDto
import net.systemvi.website.darkproject.big_title.*
import net.systemvi.website.darkproject.footer.*
import net.systemvi.website.darkproject.navbar.*
import net.systemvi.website.darkproject.navbar.Navbar
import net.systemvi.website.darkproject.section.*
import io.circe.scalajs.*
import io.circe.scalajs.EncoderJsOps.*
import io.circe.generic.*
import io.circe.generic.auto.*
import net.systemvi.website.KeyboardPage
import org.scalajs.dom

def KeyboardsPageView(): HtmlElement = {

  val response = dom.fetch("http://localhost:8080/api/keyboards")

  val keyboards = Var[List[KeyboardDto]](List.empty)

  response.`then`{ response=>
    response.json().`then`{json=>
      val list = decodeJs[List[KeyboardDto]](json).getOrElse(List.empty)
      keyboards.writer.onNext(list)
    }
  }

  div(
    cls:="flex flex-col items-center pt-24",
    div(
      className:="flex flex-col justify-start w-full max-w-[1450px]",
      Navbar(),
      BigTitle("Keyboards"),
      child <-- keyboards.signal.map{ keyboards =>
        Section(
          title = "",
          items = keyboards.map(k => SectionItem(k.name, k.images.head.imageUrl, KeyboardPage(k.uuid)))
        )
      },
      Footer(),
    )
  )
}
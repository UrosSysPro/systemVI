package net.systemvi.website.views

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.common.dtos.ApplicationDto
import net.systemvi.website.*
import net.systemvi.website.api.GameApi
import net.systemvi.website.darkproject.big_title.BigTitle
import net.systemvi.website.darkproject.footer.Footer
import net.systemvi.website.darkproject.neo_navbar.NeoNavbar
import net.systemvi.website.darkproject.section.{Section, SectionItem}
import net.systemvi.website.darkproject.slider.ImageSlider
import cats.*
import cats.implicits.*
import io.circe.scalajs.*
import io.circe.scalajs.EncoderJsOps.*
import io.circe.generic.*
import io.circe.generic.auto.*
import net.systemvi.website.*
import org.scalajs.dom

def ApplicationsPageView(
                          title: String,
                          appsSignal: Signal[List[ApplicationDto]],
                          showImageSlider: Boolean
                        ): HtmlElement = {
  div(
    className("flex flex-col items-center pt-24"),
    div(
      className("flex flex-col justify-start w-full max-w-[1450px]"),
      NeoNavbar(),
      BigTitle(title),
      child <-- appsSignal.map{ apps =>
        if showImageSlider then
          ImageSlider(apps.flatMap(_.images.map(_.imageUrl)))
        else
          emptyNode
      },
      child <-- appsSignal.map{ apps =>
        Section(
          title = "",
          items = apps.map{ app =>
            SectionItem(
              app.name,
              app.images.map(_.imageUrl).head,
              HomePage
            )
          }
        )
      },
      Footer(),
    )
  )
}

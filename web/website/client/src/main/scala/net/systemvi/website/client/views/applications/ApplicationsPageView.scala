package net.systemvi.website.client.views.applications

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.website.*
import cats.*
import cats.implicits.*
import io.circe.scalajs.*
import io.circe.scalajs.EncoderJsOps.*
import io.circe.generic.*
import io.circe.generic.auto.*
import net.systemvi.website.*
import net.systemvi.website.client.api.GameApi
import net.systemvi.website.client.darkproject.big_title.BigTitle
import net.systemvi.website.client.darkproject.footer.Footer
import net.systemvi.website.client.darkproject.neo_navbar.NeoNavbar
import net.systemvi.website.client.darkproject.section.{Section, SectionItem}
import net.systemvi.website.client.darkproject.slider.ImageSlider
import net.systemvi.website.client.routes.Pages.*
import net.systemvi.website.client.views.*
import net.systemvi.website.client.views.applications.*
import net.systemvi.website.common.dtos.ApplicationDto
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
              ApplicationDetailsPage(app.uuid)
            )
          }
        )
      },
      Footer(),
    )
  )
}

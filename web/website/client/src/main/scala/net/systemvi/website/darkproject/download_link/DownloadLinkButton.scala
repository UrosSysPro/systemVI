package net.systemvi.website.darkproject.download_link

import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom
import net.systemvi.common.model.DownloadLink
import net.systemvi.website.darkproject.Theme

def DownloadLinkButton (links:List[DownloadLink]):HtmlElement={
  val selected=Var(0)
  div(
    display.flex,
    paddingLeft.rem:=1,
    paddingRight.rem:=1,
    paddingTop.rem:=0.5f,
    paddingBottom.rem:=0.5f,
    gap.rem:=1,
    Theme.common.roundedXL,
    backgroundColor:="#515151",
    a(
      color:="white",
      flex:="1",
      download:="true",
      href<--selected.signal.map{index=>links(index).url},
      text<--selected.signal.map{index=>links(index).name},
    ),
    div(
      width.px:=2,
      height.percent:=100,
      backgroundColor:="white",
    ),
    button(
      color:="white",
      onClick-->{event => dom.console.log("click")},
      "->"
    ),
  )
}

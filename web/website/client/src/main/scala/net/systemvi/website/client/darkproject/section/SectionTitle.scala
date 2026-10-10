package net.systemvi.website.client.darkproject.section

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.website.client.routes.Router
import net.systemvi.website.client.routes.Pages.*


def SectionTitle(text:String,viewAllPage:Page=null):HtmlElement=div(
  display.flex,
  justifyContent.spaceBetween,
  width.percent:=100,
  span(
    fontWeight.bold,
    fontSize:="2rem",
    text
  ),
  if viewAllPage!=null then div(
    display.flex,
    justifyContent.center,
    alignItems.center,
    a(
      Router.navigateTo(viewAllPage),
      borderRadius:="0.3rem",
      backgroundColor:="#eee",
      transition:="300ms",
      paddingLeft.rem:=2,
      paddingRight.rem:=2,
      paddingTop:="0.5rem",
      paddingBottom:="0.5rem",
      "View All ->"
    )
  )
  else emptyNode
)


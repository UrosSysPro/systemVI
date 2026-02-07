package net.systemvi.website.darkproject.section

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.website.routes.Pages.*
import net.systemvi.website.routes.Router

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


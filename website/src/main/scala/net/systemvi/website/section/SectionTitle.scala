package net.systemvi.website.section

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.website.Page
import net.systemvi.website.router

def SectionTitle(text:String,viewAllPage:Page=null):Element=div(
  display.flex,
  justifyContent.spaceBetween,
  width.percent:=100,
  span(
    className:="text-2xl font-bold",
    text
  ),
  if viewAllPage!=null then a(
    router.navigateTo(viewAllPage),
    borderRadius:="0.3rem",
    backgroundColor:="#eee",
    paddingLeft.rem:=2,
    paddingRight.rem:=2,
    paddingTop:="0.5rem",
    paddingBottom:="0.5rem",
    "View All ->"
  ) else emptyNode
)


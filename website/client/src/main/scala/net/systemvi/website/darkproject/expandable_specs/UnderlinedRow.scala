package net.systemvi.website.darkproject.expandable_specs

import com.raquo.laminar.api.L.{*, given}

def UnderlinedRow(name:String,value:String):HtmlElement={

  div(
    display.flex,
    paddingTop := "0.5rem",
    paddingBottom := "0.5rem",
    justifyContent.spaceBetween,
    borderBottom := "1px solid rgba(51,51,51,0.5)",
    span(
      color := "#626569",
      fontSize.rem := 1,
      fontWeight := "500",
      name
    ),
    span(
      color := "#22272d",
      fontSize.rem := 1,
      fontWeight := "600",
      value
    )
  )}
package net.systemvi.website

import com.raquo.laminar.api.L.{*, given}

def AboutSectionTitle():Element = span(
  className:= "text-2xl font-bold px-3"
)

def AboutSectionCard(number:String, title:String, text:String):Element = div(
  span(
    className:="",
    number
  ),
  span(title),
  span(text),
)

def AboutSection():Element = div(
  className := "flex flex-col p-4",
  AboutSectionTitle(),
  div(
    className:="flex gap-6",
    AboutSectionCard("01", "Lorem Ipsum",
      """bla bla bla
        |bla bla bla
        |bla bla bla
        |bla bla bla
        |bla bla bla
        |bla bla bla
        |""".stripMargin),
    AboutSectionCard("01", "Lorem Ipsum",
      """bla bla bla
        |bla bla bla
        |bla bla bla
        |bla bla bla
        |bla bla bla
        |bla bla bla
        |""".stripMargin)
  )
)
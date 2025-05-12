package net.systemvi.website.section

import com.raquo.laminar.api.L.{*, given}

def SectionTitle(text:String):Element=span(
  className:="text-2xl font-bold pl-3",
  text
)


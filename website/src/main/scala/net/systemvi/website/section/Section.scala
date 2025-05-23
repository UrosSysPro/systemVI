package net.systemvi.website.section

import com.raquo.laminar.api.L.{*, given}

case class SectionItem(name:String,image:String)

def Section(title:String, items:List[SectionItem]):Element=div(
  className:="w-full flex flex-col items-start justify-start px-4 py-3",
  SectionTitle(title),
  div(
    className:="grid grid-cols-1 min-[600px]:grid-cols-2 min-[1000px]:grid-cols-3 min-[1400px]:grid-cols-4 w-full gap-6 pt-6 ",
    items.map(SectionItemCard)
  )
)
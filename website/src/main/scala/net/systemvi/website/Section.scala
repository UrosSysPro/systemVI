package net.systemvi.website

import com.raquo.laminar.api.L.{*,given}

case class SectionItem(name:String,image:String)

def SectionTitle(text:String):Element=span(
  className:="text-2xl font-bold pl-3",
  text
)

def SectionItemCard(item:SectionItem):Element=div(
  className:="flex flex-col gap-3",
  div(
    className:="w-full aspect-square bg-red-500 rounded-[16px]"
  ),
  div(
    className:="font-bold pl-3 text-xl",
    item.name
  ),
)

def Section(title:String, items:List[SectionItem]):Element=div(
  className:="w-full flex flex-col items-start justify-start px-4 py-3",
  SectionTitle(title),
  div(
    className:="pt-6 grid grid-cols-2 w-full gap-6",
    items.map(SectionItemCard)
  )
)
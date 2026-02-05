package net.systemvi.website.darkproject.section

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.website.Page

case class SectionItem(name:String,image:String,page:Page)

def Section(title: String, items: List[SectionItem], viewAllPage: Page = null):Element=div(
  className:="w-full flex flex-col items-start justify-start px-4 py-3",
  if title.isEmpty then emptyNode else SectionTitle(title,viewAllPage),
  div(
    className:="grid grid-cols-1 min-[600px]:grid-cols-2 min-[1000px]:grid-cols-3 min-[1400px]:grid-cols-4 w-full gap-6 pt-6 ",
    items.map(SectionItemCard)
  )
)
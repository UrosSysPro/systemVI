package net.systemvi.main_menu.client.views

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.mainmenu.common.dto.ItemDto
import net.systemvi.main_menu.client.api.MenuApi
import net.systemvi.main_menu.client.components.Navbar

def ItemsPageView(): HtmlElement = {
  val items = MenuApi.items.toSignal(List.empty)

  div(
    Navbar(),
    div(
      display.flex,
      flexDirection.column,
      alignItems.center,
      padding.rem(2),
      gap.rem(1),
      h1(fontSize.rem(2), fontWeight.bold, "Items"),
      child <-- items.map {
        case Nil => div("No items yet.")
        case list => div(
          display.flex,
          flexWrap.wrap,
          justifyContent.center,
          gap.rem(1),
          list.map(ItemCard),
        )
      },
    ),
  )
}

private def ItemCard(item: ItemDto): HtmlElement =
  div(
    display.flex,
    flexDirection.column,
    gap.rem(0.5),
    width.px(240),
    padding.rem(1),
    border("1px solid #dddddd"),
    borderRadius.rem(0.5),
    img(src(item.imageUrl), alt(item.name), width.percent(100), height.px(160)),
    span(fontWeight.bold, item.name),
    item.description.fold(emptyNode)(text => span(color("#666666"), text)),
    span(fontWeight.bold, color("#2e8b57"), s"${item.price} €"),
  )

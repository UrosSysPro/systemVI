package net.systemvi.main_menu.client.views

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.mainmenu.common.dto.CategoryDto
import net.systemvi.main_menu.client.api.MenuApi
import net.systemvi.main_menu.client.components.Navbar

def CategoryPageView(): HtmlElement = {
  val categories = MenuApi.categories.toSignal(List.empty)

  div(
    Navbar(),
    div(
      display.flex,
      flexDirection.column,
      alignItems.center,
      padding.rem(2),
      gap.rem(1),
      h1(fontSize.rem(2), fontWeight.bold, "Categories"),
      div(
        display.flex,
        flexDirection.column,
        gap.rem(1),
        width.percent(100),
        maxWidth.px(900),
        child <-- categories.map {
          case Nil => div("No categories yet.")
          case list => div(
            display.flex,
            flexDirection.column,
            gap.rem(1),
            list.map(CategoryCard),
          )
        },
      ),
    ),
  )
}

private def CategoryCard(category: CategoryDto): HtmlElement =
  div(
    display.flex,
    flexDirection.column,
    gap.rem(0.5),
    padding.rem(1),
    border("1px solid #dddddd"),
    borderRadius.rem(0.5),
    div(
      display.flex,
      flexDirection.row,
      alignItems.center,
      gap.rem(1),
      img(src(category.imageUrl), alt(category.name), width.px(48), height.px(48), borderRadius.rem(0.25)),
      div(
        display.flex,
        flexDirection.column,
        span(fontWeight.bold, category.name),
        category.description.fold(emptyNode)(text => span(color("#666666"), text)),
      ),
    ),
    if category.children.isEmpty then emptyNode
    else div(
      display.flex,
      flexDirection.column,
      gap.rem(0.5),
      paddingLeft.rem(1),
      borderLeft("2px solid #eeeeee"),
      category.children.map(CategoryCard),
    ),
  )

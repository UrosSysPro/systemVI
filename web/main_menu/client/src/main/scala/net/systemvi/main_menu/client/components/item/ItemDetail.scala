package net.systemvi.main_menu.client.components.item

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.mainmenu.common.dto.ItemDto
import net.systemvi.main_menu.client.styles.CSSProps.*

/**
 * A single item laid out vertically: picture, then name and description, with the
 * price on a hairline footer.
 *
 * `actions` renders at the end of that footer, which is where the table page will
 * put its add-to-cart button.
 */
def ItemDetail(item: ItemDto, actions: List[HtmlElement] = Nil): HtmlElement =
  div(
    flexColumn,
    gap.rem(1),
    width.percent(100),
    img(
      src := item.imageUrl,
      alt := item.name,
      width.percent(100),
      aspect := "4 / 3",
      objectFit := "cover",
      borderRadius.rem(0.75),
      backgroundColor("#f0f1f3"),
    ),
    div(
      flexColumn,
      gap.rem(0.5),
      h1(fontSize.rem(1.75), fontWeight.bold, color("#22272d"), item.name),
      item.description.map(_.trim).filter(_.nonEmpty).fold(emptyNode) { description =>
        p(fontSize.rem(1), color("#4b5563"), lineHeight := "1.5", description)
      },
    ),
    div(
      flexRow,
      alignItems.center,
      justifyContent.spaceBetween,
      gap.rem(1),
      paddingTop.rem(0.75),
      borderTop("1px solid #ececec"),
      span(fontSize.rem(1.25), fontWeight.bold, color("#2e8b57"), s"${item.price} €"),
      actions,
    ),
  )

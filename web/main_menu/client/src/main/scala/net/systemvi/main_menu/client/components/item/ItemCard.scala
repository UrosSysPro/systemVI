package net.systemvi.main_menu.client.components.item

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.mainmenu.common.dto.ItemDto
import net.systemvi.main_menu.client.routes.Pages.*
import net.systemvi.main_menu.client.routes.Router
import net.systemvi.main_menu.client.styles.CSSProps.*

/**
 * A menu item row: image on the left, name and description stacked vertically in
 * the middle, price pinned to the end. Tapping it navigates to `destination`,
 * which is the read-only item page or the table one depending on the caller.
 */
def ItemCard(item: ItemDto, destination: Page): HtmlElement =
  val hovered = Var(false)

  a(
    Router.navigateTo(destination),
    flexRow,
    alignItems.center,
    gap.rem(1),
    padding.rem(0.75),
    cardSurface,
    color("#22272d"),
    textDecoration("none"),
    cursor.pointer,
    transition("box-shadow 150ms ease, border-color 150ms ease"),
    borderColor <-- hovered.signal.map(if _ then "#cfd4da" else "#e8e8ea"),
    boxShadow <-- hovered.signal.map(
      if _ then "0 6px 16px rgba(16, 24, 40, 0.10)" else "0 1px 2px rgba(16, 24, 40, 0.04)"
    ),
    onMouseEnter --> (_ => hovered.set(true)),
    onMouseLeave --> (_ => hovered.set(false)),
    img(
      src := item.imageUrl,
      alt := item.name,
      width.px(84),
      height.px(84),
      aspect := "1 / 1",
      objectFit := "cover",
      flexShrink := 0.0,
      borderRadius.rem(0.5),
      backgroundColor("#f0f1f3"),
    ),
    div(
      flexColumn,
      flex("1"),
      minWidth.px(0),
      gap.rem(0.125),
      span(
        fontSize.rem(1.05),
        fontWeight.bold,
        color("#22272d"),
        whiteSpace.nowrap,
        textOverflow.ellipsis,
        overflow.hidden,
        item.name,
      ),
      item.description.map(_.trim).filter(_.nonEmpty).fold(emptyNode) { description =>
        span(
          fontSize.rem(0.9),
          color("#6b7280"),
          lineHeight := "1.4",
          clampLines(2),
          description,
        )
      },
    ),
    span(
      fontSize.rem(1.05),
      fontWeight.bold,
      color("#2e8b57"),
      whiteSpace.nowrap,
      flexShrink := 0.0,
      s"${item.price} €",
    ),
  )

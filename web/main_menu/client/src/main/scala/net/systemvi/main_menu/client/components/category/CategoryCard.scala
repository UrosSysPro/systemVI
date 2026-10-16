package net.systemvi.main_menu.client.components.category

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.mainmenu.common.dto.CategoryDto
import net.systemvi.main_menu.client.routes.Pages.*
import net.systemvi.main_menu.client.routes.Router
import net.systemvi.main_menu.client.styles.CSSProps.*

/**
 * A category row: the same shape as [[net.systemvi.main_menu.client.components.item.ItemCard]]
 * with the description and price left off, since categories carry neither.
 *
 * Tapping it navigates to `destination`, which lets the end-user menu and the
 * admin pages share one card and differ only in where it points.
 */
def CategoryCard(category: CategoryDto, destination: Page): HtmlElement =
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
      src := category.imageUrl,
      alt := category.name,
      width.px(64),
      height.px(64),
      aspect := "1 / 1",
      objectFit := "cover",
      flexShrink := 0.0,
      borderRadius.rem(0.5),
      backgroundColor("#f0f1f3"),
    ),
    span(
      flex("1"),
      minWidth.px(0),
      fontSize.rem(1.05),
      fontWeight.bold,
      whiteSpace.nowrap,
      textOverflow.ellipsis,
      overflow.hidden,
      category.name,
    ),
    span(color("#9aa0a6"), ">"),
  )

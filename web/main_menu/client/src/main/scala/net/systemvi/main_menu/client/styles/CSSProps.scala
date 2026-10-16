package net.systemvi.main_menu.client.styles

import com.raquo.laminar.api.L.{*, given}

/**
 * The home for CSS we cannot express with Laminar's built-in style props, plus the
 * small set of reusable style utilities the app shares.
 *
 * Everything else is styled inline on the element itself, so views read as plain
 * Laminar and never depend on a CSS build step. Add a utility here only once more
 * than one component needs it.
 */
object CSSProps {

  // -- Properties Laminar does not ship ---------------------------------------

  /** `aspect-ratio`, e.g. `aspect := "1 / 1"`. */
  val aspect: StyleProp[String] = styleProp[String]("aspect-ratio")

  /** `object-fit`, e.g. `objectFit := "cover"` on an `img`. */
  val objectFit: StyleProp[String] = styleProp[String]("object-fit")

  /** `object-position`. */
  val objectPosition: StyleProp[String] = styleProp[String]("object-position")

  /** `backdrop-filter`. */
  val backdropFilter: StyleProp[String] = styleProp[String]("backdrop-filter")

  /** `user-select`. */
  val userSelect: StyleProp[String] = styleProp[String]("user-select")

  /** `grid-template-columns` — see also [[grid]]. */
  val gridTemplateColumns: StyleProp[String] = styleProp[String]("grid-template-columns")

  /** `-webkit-line-clamp` — reach for [[clampLines]] instead. */
  private val lineClamp: StyleProp[String] = styleProp[String]("-webkit-line-clamp")

  /** `-webkit-box-orient` — reach for [[clampLines]] instead. */
  private val webkitBoxOrient: StyleProp[String] = styleProp[String]("-webkit-box-orient")

  // -- Utilities --------------------------------------------------------------

  /** Horizontal flex container. */
  val flexRow: Modifier[HtmlElement] =
    List[Modifier[HtmlElement]](display.flex, flexDirection.row)

  /** Vertical flex container. */
  val flexColumn: Modifier[HtmlElement] =
    List[Modifier[HtmlElement]](display.flex, flexDirection.column)

  /** Responsive grid of cards, e.g. `grid("repeat(auto-fill, minmax(320px, 1fr))")`. */
  def grid(columns: String, gapRem: Double = 0.75): Modifier[HtmlElement] =
    List[Modifier[HtmlElement]](
      display.grid,
      gridTemplateColumns := columns,
      gap.rem(gapRem),
    )

  /** White card surface: hairline border on a rounded corner. */
  val cardSurface: Modifier[HtmlElement] =
    List[Modifier[HtmlElement]](
      backgroundColor("#ffffff"),
      border("1px solid #e8e8ea"),
      borderRadius.rem(0.75),
    )

  /**
   * Truncate text to `lines` lines with a trailing ellipsis. Needs the
   * `-webkit-box` display mode, which is why the pair ships together here
   * rather than being spelled out at each call site.
   */
  def clampLines(lines: Int): Modifier[HtmlElement] =
    List[Modifier[HtmlElement]](
      display := "-webkit-box",
      webkitBoxOrient := "vertical",
      lineClamp := lines.toString,
      overflow.hidden,
    )
}

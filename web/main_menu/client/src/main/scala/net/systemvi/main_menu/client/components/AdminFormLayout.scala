package net.systemvi.main_menu.client.components

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.codecs
import net.systemvi.main_menu.client.routes.Pages.*
import net.systemvi.main_menu.client.routes.Router
import net.systemvi.main_menu.client.styles.CSSProps.*

/**
 * Shared scaffolding for the standalone admin create/update pages.
 *
 * Each of those pages is its own component with its own route, but they all want
 * the same shell: a back link, a heading, a column of form fields and a row of
 * submit/cancel actions. Keeping that here means the four pages stay consistent
 * and only their fields differ.
 */
object FormPage {

  /** A labelled form field. `hint` shows under the input, e.g. "required". */
  def field(
    label: String,
    control: HtmlElement,
    hint: Option[String] = None,
  ): HtmlElement =
    div(
      flexColumn,
      gap.rem(0.25),
      span(fontSize.rem(0.9), fontWeight.bold, color("#4b5563"), label),
      control,
      hint.fold(emptyNode)(text => span(fontSize.rem(0.8), color("#9aa0a6"), text)),
    )

  /** A bordered block grouping related fields. */
  def section(title: String, children: List[HtmlElement]): HtmlElement =
    div(
      flexColumn,
      gap.rem(1),
      padding.rem(1.25),
      cardSurface,
      h2(fontSize.rem(1.1), fontWeight.bold, color("#22272d"), title),
      children,
    )

  /** The submit/cancel row. */
  def actions(children: List[HtmlElement]): HtmlElement =
    div(flexRow, alignItems.center, gap.rem(0.75), children)

  /** A full-width text input bound to `model`. */
  def textInput(placeholderText: String, model: Var[String]): HtmlElement =
    input(
      placeholder(placeholderText),
      value <-- model.signal,
      onInput.mapToValue --> model,
      padding.rem(0.6),
      border("1px solid #d5d8dd"),
      borderRadius.rem(0.5),
      width.percent(100),
      backgroundColor("#ffffff"),
    )

  /** A full-width multi-line input bound to `model`. */
  def textInputArea(placeholderText: String, model: Var[String], rows: Int = 3): HtmlElement =
    textArea(
      placeholder(placeholderText),
      value <-- model.signal,
      onInput.mapToValue --> model,
      rowsAttr(rows),
      padding.rem(0.6),
      border("1px solid #d5d8dd"),
      borderRadius.rem(0.5),
      width.percent(100),
      resize := "vertical",
      backgroundColor("#ffffff"),
    )

  /** Laminar has no `rows` attribute helper, so it is declared here. */
  private val rowsAttr: HtmlAttr[Int] = htmlAttr[Int]("rows", codecs.IntAsStringCodec)

  /** The primary submit button, disabled while `busy`. */
  def submitButton(label: String, busy: Signal[Boolean], handler: Modifier[HtmlElement]): HtmlElement =
    button(
      label,
      handler,
      disabled <-- busy,
      backgroundColor <-- busy.map(if _ then "#c9cfda" else "#2e8b57"),
      color("#ffffff"),
      fontWeight.bold,
      paddingTop.rem(0.6),
      paddingBottom.rem(0.6),
      paddingLeft.rem(1.2),
      paddingRight.rem(1.2),
      border("none"),
      borderRadius.rem(0.5),
      cursor.pointer,
    )

  /** A secondary "go back" button built from a navigation modifier. */
  def cancelButton(label: String, navigate: Modifier[HtmlElement]): HtmlElement =
    a(
      navigate,
      label,
      display.inlineBlock,
      paddingTop.rem(0.6),
      paddingBottom.rem(0.6),
      paddingLeft.rem(1.2),
      paddingRight.rem(1.2),
      border("1px solid #d5d8dd"),
      borderRadius.rem(0.5),
      backgroundColor("#ffffff"),
      color("#4b5563"),
      fontWeight.bold,
      textDecoration("none"),
      cursor.pointer,
    )

  /** The last failure, if any, as a red banner. */
  def errorBanner(message: Signal[Option[String]]): HtmlElement =
    div(
      display <-- message.map(_.fold("none")(_ => "block")),
      padding.rem(0.75),
      borderRadius.rem(0.5),
      backgroundColor("#fdecec"),
      border("1px solid #f5c2c2"),
      color("#a12626"),
      fontSize.rem(0.9),
      child.text <-- message.map(_.getOrElse("")),
    )

  /** Placeholder shown while the record being edited is still loading. */
  def loading(message: String = "Loading…"): HtmlElement =
    div(color("#6b7280"), message)
}

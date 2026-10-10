package net.systemvi.main_menu.client.components

import com.raquo.laminar.api.L.{*, given}

def styledInput(placeholderText: String, model: Var[String], inputWidth: Int = 220): HtmlElement =
  input(
    placeholder(placeholderText),
    value <-- model.signal,
    onInput.mapToValue --> model,
    padding.rem(0.5),
    border("1px solid #cccccc"),
    borderRadius.rem(0.25),
    width.px(inputWidth),
  )

def actionButton(label: String, handler: Modifier[HtmlElement], background: String = "#dddddd"): HtmlElement =
  button(
    label,
    handler,
    paddingTop.rem(0.5),
    paddingBottom.rem(0.5),
    paddingLeft.rem(0.75),
    paddingRight.rem(0.75),
    border("1px solid #bbbbbb"),
    borderRadius.rem(0.25),
    backgroundColor(background),
    cursor.pointer,
  )

def reactiveActionButton(label: Signal[String], handler: Modifier[HtmlElement], background: String = "#dddddd"): HtmlElement =
  button(
    child.text <-- label,
    handler,
    paddingTop.rem(0.5),
    paddingBottom.rem(0.5),
    paddingLeft.rem(0.75),
    paddingRight.rem(0.75),
    border("1px solid #bbbbbb"),
    borderRadius.rem(0.25),
    backgroundColor(background),
    cursor.pointer,
  )

def formRow(children: List[HtmlElement]): HtmlElement =
  div(display.flex, flexDirection.row, flexWrap.wrap, gap.rem(0.5), alignItems.center, children)

def optionalText(value: String): Option[String] =
  val trimmed = value.trim
  if trimmed.isEmpty then None else Some(trimmed)

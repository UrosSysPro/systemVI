package net.systemvi.website.section

import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom

private def AboutSectionItem(number:String,title:String,body:String):HtmlElement={
  div(
    borderRadius.rem:=1,
    backgroundColor.rgb(246,246,246),
    display.flex,
    flexDirection.column,
    flex:="1",
    padding.rem:=2,
    height:="max",
    span(number,color.gray,paddingBottom.rem:=3),
    span(title,color.rgb(51,51,51),fontSize.rem:=2,paddingBottom.rem:=3),
    span(body,color.gray,fontSize:="1.4rem")
  )
}

def AboutSection():HtmlElement={
  val vertical = windowEvents(_.onResize).map { _ => dom.window.innerWidth < 700 }.startWith(dom.window.innerWidth < 700)
  div(
    padding.rem:=1,
    SectionTitle("About Us").amend(paddingBottom.rem:=1),
    div(
      display.flex,
      flexDirection <-- vertical.signal.map(if _ then "column" else "row"),
      minHeight.rem:=30,
      gap.rem:=1,
      AboutSectionItem("01","Lorem ipsum",
        """
          |Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse semper iaculis elit,
          |vel luctus ipsum pharetra at. Curabitur a lacus ullamcorper, tincidunt nisi ut,
          |ultricies nisi. Praesent ac ex mi. Sed accumsan leo sapien, a laoreet libero
          |ultricies ac. Pellentesque pellentesque,
          |""".stripMargin),
      AboutSectionItem("02","Donec viverra",
        """
          |Donec viverra fringilla posuere. Cras eu ligula tempor leo luctus ornare.
          |Duis accumsan ipsum eu venenatis rhoncus. Etiam eu mollis diam.
          |Vestibulum sed dictum leo. In in ex lobortis, imperdiet ligula nec,
          |semper dui. Praesent congue magna vitae lectus sodales, suscipit vehicula
          |lectus iaculis. Morbi et risus erat. Proin
          |""".stripMargin),
    )
  )
}

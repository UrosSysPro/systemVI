package net.systemvi.website.product_info

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.website.style.Theme
import org.scalajs.dom
import net.systemvi.website.CSSProps.*

case class Spec(name:String,value:String)
case class Product(name:String,codeName:String,specs:List[Spec],images:List[String])

def ProductInfo(product:Product):Element = div(
  height.rem:=40,
  display.flex,
  gap.rem:=2,
  paddingLeft.rem:=1,
  paddingRight.rem:=1,
  div(
    flex:="1",
    height.percent:=100,
    Theme.common.roundedXL,
    overflow.hidden,
    display.flex,
    flexDirection.column,
    gap.rem:=1,
    div(
      flex:="1",
      Theme.common.roundedXL,
      overflow.hidden,
      img(
        width.percent:=100,
        height.percent:=100,
        objectFit:="cover",
        src:=product.images(0),
        alt:=product.images(0)
      )
    ),
    div(
      display.flex,
      height.rem:=5,
      overflowX.auto,
      gap:="0.5rem",
      hiddenScrollbar,
      product.images.map{url=>img(
        src:=url,
        alt:=url,
        borderRadius.rem:=1,
      )}
    ),
  ),
  div(
    display.flex,
    flexDirection.column,
    flex:="1",
    height.percent:=100,
    Theme.common.roundedXL,
    backgroundColor:="#f6f6f6",
    overflow.hidden,
    padding.rem:=1,
    span(
      paddingBottom.rem:=1,
      color:="#9e9e9e",
      fontSize:="0.75rem",
      product.codeName
    ),
    span(
      paddingBottom.rem:=2,
      color:="#22272d",
      fontSize:="1.25rem",
      fontWeight.bolder,
      product.name
    ),
    span(
      paddingBottom.rem:=1,
      color:="#22272d",
      fontSize.rem:=1,
      fontWeight.bold,
      "Product Specs"
    ),
    for(spec<-product.specs) yield div(
      display.flex,
      paddingTop:="0.5rem",
      paddingBottom:="0.5rem",
      justifyContent.spaceBetween,
      borderBottom:="1px solid rgba(51,51,51,0.5)",
      span(
        color:="#626569",
        fontSize.rem:=1,
        fontWeight:="500",
        spec.name
      ),
      span(
        color:="#22272d",
        fontSize.rem:=1,
        fontWeight:="600",
        spec.value
      )
    )
  )
)

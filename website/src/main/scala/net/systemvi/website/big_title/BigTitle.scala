package net.systemvi.website.big_title

import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom

def BigTitle(title:String):Element={
  h1(
    padding.rem:=1,
    fontSize.rem:=6,
    color:="#22272d",
    fontWeight.bold,
    title
  )
}

package net.systemvi.website

import scala.scalajs.js
import scala.scalajs.js.annotation.*
import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom

// import javascriptLogo from "/javascript.svg"

@js.native @JSImport("/javascript.svg", JSImport.Default)
val javascriptLogo: String = js.native
//
//@main
//def Website(): Unit =
//  dom.document.querySelector("#app").innerHTML = s"""
//    <div>
//      <a href="https://vitejs.dev" target="_blank">
//        <img src="/vite.svg" class="logo" alt="Vite logo" />
//      </a>
//      <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript" target="_blank">
//        <img src="$javascriptLogo" class="logo vanilla" alt="JavaScript logo" />
//      </a>
//      <h1>Hello Scala.js!</h1>
//      <div class="card">
//        <button id="counter" type="button"></button>
//      </div>
//      <p class="read-the-docs">
//        jel ovo radi
//      </p>
//    </div>
//  """
//
//  setupCounter(dom.document.getElementById("counter"))
//end Website
//
//def setupCounter(element: dom.Element): Unit =
//  var counter = 0
//
//  def setCounter(count: Int): Unit =
//    counter = count
//    element.innerHTML = s"count is $counter"
//
//  element.addEventListener("click", e => setCounter(counter + 1))
//  setCounter(0)
//end setupCounter

@main
def LiveChart(): Unit =
  renderOnDomContentLoaded(
    dom.document.getElementById("app"),
    Main.appElement()
  )
object Main:
  def appElement(): Element =
    div(
      a(href := "https://vitejs.dev", target := "_blank",
        img(src := "/vite.svg", className := "logo", alt := "Vite logo"),
      ),
      a(href := "https://developer.mozilla.org/en-US/docs/Web/JavaScript", target := "_blank",
        img(src := javascriptLogo, className := "logo vanilla", alt := "JavaScript logo"),
      ),
      h1("Hello Laminar!"),
      div(className := "card",
        button(tpe := "button"),
      ),
      p(className := "read-the-docs",
        "Click on the Vite logo to learn more",
      ),
    )
  end appElement
end Main

package net.systemvi.website.views

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.website.KeyboardPage
import net.systemvi.website.footer.Footer
import net.systemvi.website.navbar.Navbar
import net.systemvi.website.section.{Section, SectionItem}
import net.systemvi.website.slider.ImageSlider
import org.scalajs.dom

def HomePageView():Element = {
  div(
    cls:="flex flex-col items-center pt-24",
    div(
      className:="flex flex-col justify-start w-full max-w-[1450px]",
      Navbar(),
      ImageSlider(
        images = List(
          "images/keyboards-all.jpg",
          "images/keyboards-all-rotated.jpg",
        )
      ),
      Section("Builds",List(
        SectionItem("Corne Wireless","images/corne-wireless.jpg",KeyboardPage(1)),
        SectionItem("Corne Prototype","images/corne-prototype.jpg",KeyboardPage(2)),
        SectionItem("PH Design 60%","images/keyboard-60.jpg",KeyboardPage(3)),
        SectionItem("Bana 40%","images/red-keyboard.jpg",KeyboardPage(4)),
      )),
      Section("In Progress",List(
        SectionItem("TKL Rabbit","images/tkl-rabbit.jpg",KeyboardPage(5)),
        SectionItem("7x5 Dactyl","images/dactyl2.jpg",KeyboardPage(6)),
//        SectionItem("Modded YENKEE","item2.png"),
      )),
      Section("Tools",List(
        SectionItem("Soldering Iron","item1.png",KeyboardPage(1)),
        SectionItem("Pliers","item1.png",KeyboardPage(1)),
        SectionItem("Key Switch","item1.png",KeyboardPage(1)),
        SectionItem("Key Caps","item1.png",KeyboardPage(1)),
      )),
      //  AboutSection(),
      Footer(),
    )
  )
}
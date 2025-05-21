package net.systemvi.website
import org.scalajs.dom
import com.raquo.laminar.api.L.{*, given}
import net.systemvi.website.footer.Footer
import net.systemvi.website.navbar.Navbar
import net.systemvi.website.section.{Section, SectionItem}
import net.systemvi.website.slider.ImageSlider

def Page():Element = {
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
        SectionItem("Corne Wireless","images/corne-wireless.jpg"),
        SectionItem("Corne Prototype","images/corne-prototype.jpg"),
        SectionItem("PH Design 60%","images/keyboard-60.jpg"),
        SectionItem("Bana 40%","images/red-keyboard.jpg"),
      )),
      Section("In Progress",List(
        SectionItem("TKL Rabbit","images/tkl-rabbit.jpg"),
        SectionItem("7x5 Dactyl","images/dactyl2.jpg"),
//        SectionItem("Modded YENKEE","item2.png"),
      )),
      Section("Tools",List(
        SectionItem("Soldering Iron","item1.png"),
        SectionItem("Pliers","item1.png"),
        SectionItem("Key Switch","item1.png"),
        SectionItem("Key Caps","item1.png"),
      )),
      //  AboutSection(),
      Footer(),
    )
  )
}
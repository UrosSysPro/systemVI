package net.systemvi.website

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.website.footer.Footer
import net.systemvi.website.navbar.Navbar
import net.systemvi.website.section.{Section, SectionItem}
import net.systemvi.website.slider.ImageSlider

def Page():Element = div(
  className:="flex flex-col justify-start items-center w-full",
  Navbar(),
  ImageSlider(
    images = List(
      "images/keyboards-all.jpg" 
    )
  ),
  Section("Builds",List(
    SectionItem("Corne Wireless","images/corne-wireless.jpg"),
    SectionItem("Corne Prototype","images/corne-prototype.jpg"),
    SectionItem("PH Design 60%","images/keyboard-60.jpg"),
    SectionItem("Bana 40%","images/red-keyboard.jpg"),
  )),
  Section("Section 1",List(
    SectionItem("item 1","item1.png"),
    SectionItem("item 1","item1.png"),
    SectionItem("item 1","item1.png"),
  )),
  Section("Section 1",List(
    SectionItem("item 1","item1.png"),
    SectionItem("item 1","item1.png"),
    SectionItem("item 1","item1.png"),
  )),
//  AboutSection(),
  Footer(),
)
package net.systemvi.website

import com.raquo.laminar.api.L.{*, given}

def Page():Element = div(
  className:="flex flex-col justify-start items-center w-full",
  Navbar(),
  ImageSlider(),
  Section("Section 1",List(
    SectionItem("corne-wireless.jpg","images/corne-wireless.jpg"),
    SectionItem("item 1","item1.png"),
    SectionItem("item 1","item1.png"),
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
  AboutSection(),
  Footer(),
)
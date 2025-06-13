package net.systemvi.website.api

import net.systemvi.common.model.{Engine, EngineDemo, Game, Keyboard, ProductSpec}

private val keyboards=List[Keyboard](
  Keyboard(
    id = 1,
    name = "Corne Wireless",
    codeName = "corne_wireless",
    specs = List(
      ProductSpec(name = "spec 1", value = "value 1"),
      ProductSpec(name = "spec 2", value = "value 2"),
      ProductSpec(name = "spec 3", value = "value 3"),
      ProductSpec(name = "spec 4", value = "value 4"),
    ),
    images = List(
      "/images/corne-wireless.jpg",
      "/images/corne-wireless.jpg",
      "/images/corne-wireless.jpg",
      "/images/corne-wireless.jpg",
      "/images/corne-wireless.jpg",
      "/images/corne-wireless.jpg",
      "/images/corne-wireless.jpg",
      "/images/corne-wireless.jpg",
    )
  ),
  Keyboard(
    id = 2,
    name = "Corne Prototype",
    codeName = "corne_prototype",
    specs = List(
      ProductSpec(name = "spec 1", value = "value 1"),
      ProductSpec(name = "spec 2", value = "value 2"),
      ProductSpec(name = "spec 3", value = "value 3"),
      ProductSpec(name = "spec 4", value = "value 4"),
    ),
    images = List(
      "/images/corne-prototype.jpg",
      "/images/corne-prototype.jpg",
      "/images/corne-prototype.jpg",
      "/images/corne-prototype.jpg",
      "/images/corne-prototype.jpg",
      "/images/corne-prototype.jpg",
      "/images/corne-prototype.jpg",
      "/images/corne-prototype.jpg",
      "/images/corne-prototype.jpg",
    )
  ),
  Keyboard(
    id = 3,
    name = "PH Design 60%",
    codeName = "ph_design_60",
    specs = List(
      ProductSpec(name = "spec 1", value = "value 1"),
      ProductSpec(name = "spec 2", value = "value 2"),
      ProductSpec(name = "spec 3", value = "value 3"),
      ProductSpec(name = "spec 4", value = "value 4"),
    ),
    images = List(
      "/images/keyboard-60.jpg",
      "/images/keyboard-60.jpg",
      "/images/keyboard-60.jpg",
      "/images/keyboard-60.jpg",
      "/images/keyboard-60.jpg",
      "/images/keyboard-60.jpg",
      "/images/keyboard-60.jpg",
    )
  ),
  Keyboard(
    id = 4,
    name = "Bana 40%",
    codeName = "bana_40",
    specs = List(
      ProductSpec(name = "spec 1", value = "value 1"),
      ProductSpec(name = "spec 2", value = "value 2"),
      ProductSpec(name = "spec 3", value = "value 3"),
      ProductSpec(name = "spec 4", value = "value 4"),
    ),
    images = List(
      "/images/red-keyboard.jpg",
      "/images/red-keyboard.jpg",
      "/images/red-keyboard.jpg",
      "/images/red-keyboard.jpg",
      "/images/red-keyboard.jpg",
      "/images/red-keyboard.jpg",
      "/images/red-keyboard.jpg",
      "/images/red-keyboard.jpg",
    )
  ),
  Keyboard(
    id = 5,
    name = "TKL Rabbit",
    codeName = "tkl_rabbit",
    specs = List(
      ProductSpec(name = "spec 1", value = "value 1"),
      ProductSpec(name = "spec 2", value = "value 2"),
      ProductSpec(name = "spec 3", value = "value 3"),
      ProductSpec(name = "spec 4", value = "value 4"),
    ),
    images = List(
      "/images/tkl-rabbit.jpg",
      "/images/tkl-rabbit.jpg",
      "/images/tkl-rabbit.jpg",
      "/images/tkl-rabbit.jpg",
      "/images/tkl-rabbit.jpg",
      "/images/tkl-rabbit.jpg",
      "/images/tkl-rabbit.jpg",
    )
  ),
  Keyboard(
    id = 6,
    name = "7x5 Dactyl",
    codeName = "dactyl",
    specs = List(
      ProductSpec(name = "spec 1", value = "value 1"),
      ProductSpec(name = "spec 2", value = "value 2"),
      ProductSpec(name = "spec 3", value = "value 3"),
      ProductSpec(name = "spec 4", value = "value 4"),
    ),
    images = List(
      "/images/dactyl2.jpg",
      "/images/dactyl2.jpg",
      "/images/dactyl2.jpg",
      "/images/dactyl2.jpg",
      "/images/dactyl2.jpg",
      "/images/dactyl2.jpg",
      "/images/dactyl2.jpg",
    )
  ),
)

private val defaultKeyboard=Keyboard(
  id = -1,
  name = "Default Keyboard",
  codeName = "default_code_name",
  specs = List(
    ProductSpec(name = "spec 1", value = "value 1"),
  ),
  images = List(
    "/images/corne-wireless.jpg",
  )
)

object KeyboardApi {
  def all():List[Keyboard]=keyboards
  def get(id:Int):Keyboard=keyboards.find(_.id==id).getOrElse(defaultKeyboard)
}


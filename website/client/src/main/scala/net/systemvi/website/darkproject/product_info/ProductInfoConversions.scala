package net.systemvi.website.darkproject.product_info

import net.systemvi.common.dtos.*
import net.systemvi.common.model.{Application, Game, Keyboard, ProductSpec}

given Conversion[KeyboardDto,ProductInfoParams] = k => ProductInfoParams(
  k.images.map(i=>i.imageUrl),
  k.name,
  k.codeName,
  k.specs.map{s=>ProductSpec(s.key,s.value)},
)

given Conversion[ApplicationDto,ProductInfoParams] = a => ProductInfoParams(
  a.images.map(i=>i.imageUrl),
  a.name,
  a.codeName,
  List.empty,
)

given Conversion[Keyboard,ProductInfoParams]= keyboard => ProductInfoParams(keyboard.images,keyboard.name,keyboard.codeName,keyboard.specs)
given Conversion[Game, ProductInfoParams] = game => ProductInfoParams(game.images, game.name, game.codeName, game.specs)
given Conversion[Application, ProductInfoParams] = app => ProductInfoParams(app.screenshots, app.name, app.codeName, List(),app.downloadLinks)

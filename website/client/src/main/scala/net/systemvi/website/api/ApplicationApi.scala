package net.systemvi.website.api

import net.systemvi.common.model.Application

private val applications=List(
  Application(
    id=0,
    name="Keyboard Configurator",
    description = "Application for creating, editing, saving keymaps for custom mechanical keyboards",
    codeName = "keyboard_configurator",
    version = "v0.9.0",
    screenshots = List(
      "images/application/configurator/settings.png",
      "images/application/configurator/save-and-load.png",
      "images/application/configurator/tester.png",
      "images/application/configurator/configurator.png",
    ),
    downloadLinks = List(),
  )
)

object ApplicationApi {
  def get(id: Int): Option[Application] =applications.find(app => app.id == id)
  def all(): List[Application] =applications
}

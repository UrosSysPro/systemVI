package net.systemvi.server.persistance.models

import java.util.UUID

case class Keyboard(
  uuid:UUID,
  switchUUID:UUID,
  profileId:Int,
  name:String,
  codeName:String
)
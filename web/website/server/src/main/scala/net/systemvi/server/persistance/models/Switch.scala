package net.systemvi.server.persistance.models

import java.util.UUID

case class Switch(
                   uuid:UUID,
                   manufacturerUUID:UUID,
                   switchTypeId:Int,
                   name:String,
                 )


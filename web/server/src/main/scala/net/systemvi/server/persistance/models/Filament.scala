package net.systemvi.server.persistance.models

import java.util.UUID

case class Filament(
                    uuid: UUID,
                    polymerId: Int,
                    manufacturerUUID: UUID,
                    name: String,
                   )
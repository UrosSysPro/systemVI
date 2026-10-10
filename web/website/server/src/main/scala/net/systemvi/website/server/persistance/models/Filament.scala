package net.systemvi.website.server.persistance.models

import java.util.UUID

case class Filament(
                    uuid: UUID,
                    polymerId: Int,
                    manufacturerUUID: UUID,
                    name: String,
                   )
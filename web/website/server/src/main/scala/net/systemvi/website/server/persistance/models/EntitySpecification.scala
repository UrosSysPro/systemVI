package net.systemvi.website.server.persistance.models

import java.util.UUID

case class EntitySpecification (
                                entityUUID: UUID,
                                key: String,
                                value: String,
                                order: Int,
                               )


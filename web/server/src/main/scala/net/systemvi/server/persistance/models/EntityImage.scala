package net.systemvi.server.persistance.models

import java.util.UUID

case class EntityImage(
                        entityUUID: UUID,
                        imageUrl: String,
                        order: Int
                      )

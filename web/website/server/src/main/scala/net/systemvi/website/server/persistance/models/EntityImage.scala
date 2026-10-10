package net.systemvi.website.server.persistance.models

import java.util.UUID

case class EntityImage(
                        entityUUID: UUID,
                        imageUrl: String,
                        order: Int
                      )

package net.systemvi.website.server.persistance.models

import java.util.UUID

case class Application(
                        uuid: UUID,
                        categoryId: Int,
                        name: String,
                        codeName: String,
                        description: String,
                      )


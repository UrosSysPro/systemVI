package net.systemvi.server.persistance.models

import java.util.UUID

case class Application(
                        uuid: UUID,
                        categoryId: Int,
                        name: String,
                        codeName: String,
                        description: String,
                      )


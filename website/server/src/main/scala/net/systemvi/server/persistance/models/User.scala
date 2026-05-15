package net.systemvi.server.persistance.models

import java.util.UUID

case class User (
                  uuid: UUID,
                  email: String,
                  name: String,
                  picture: String,
                )
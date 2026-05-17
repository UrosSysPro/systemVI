package net.systemvi.server.persistance.models

import java.util.UUID

case class GoogleAccount(
                          sub: String,
                          userUUID: UUID,
                          email: String,
                          email_verified: Boolean,
                          name: Option[String],
                          picture: Option[String],
                          given_name: Option[String],
                          family_name: Option[String],
                          locale: Option[String]
                        )
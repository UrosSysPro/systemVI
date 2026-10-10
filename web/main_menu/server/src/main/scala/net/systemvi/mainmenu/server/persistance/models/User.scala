package net.systemvi.mainmenu.server.persistance.models

import java.util.UUID

/**
 * An application user, whatever the role. Customers begin as `Anonymous` and
 * become `Active` once a sign-in identity is attached.
 */
case class User(
  uuid: UUID,
  role: Role,
  status: UserStatus,
  name: Option[String],
  email: Option[String],
)

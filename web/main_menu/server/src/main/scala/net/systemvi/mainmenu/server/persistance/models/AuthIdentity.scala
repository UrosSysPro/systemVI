package net.systemvi.mainmenu.server.persistance.models

import java.util.UUID

/**
 * Links an external sign-in identity to a user.
 *
 * The pair (provider, providerSubject) is unique, so a returning sign-in can
 * find the same account across devices.
 */
case class AuthIdentity(
  uuid: UUID,
  userId: UUID,
  provider: AuthProvider,
  providerSubject: String,
  email: Option[String],
)

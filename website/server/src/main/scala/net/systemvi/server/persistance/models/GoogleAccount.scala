package net.systemvi.server.persistance.models

case class GoogleAccount(
                          sub: String,
                          email: String,
                          email_verified: Boolean,
                          name: Option[String],
                          picture: Option[String],
                          given_name: Option[String],
                          family_name: Option[String],
                          locale: Option[String]
                        )
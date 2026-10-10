package net.systemvi.mainmenu.server.persistance.models

/**
 * An external sign-in provider.
 *
 * Modelled as a Scala enum over an integer id, so an identity stores a single
 * `provider` column that a doobie `Read` maps back to the case object.
 */
sealed trait AuthProvider {
  /** Human-readable provider name, e.g. `GOOGLE`. */
  def name: String
  def id: Int
}

object AuthProvider {
  case object Google extends AuthProvider {
    override val id: Int = 1
    override val name: String = "GOOGLE"
  }

  val all: List[AuthProvider] = List(Google)

  private val byId: Map[Int, AuthProvider] = all.map(provider => provider.id -> provider).toMap

  def fromId(id: Int): Option[AuthProvider] = byId.get(id)

  def unsafeFromId(id: Int): AuthProvider =
    fromId(id).getOrElse(throw new IllegalArgumentException(s"Unknown auth provider id: $id"))
}

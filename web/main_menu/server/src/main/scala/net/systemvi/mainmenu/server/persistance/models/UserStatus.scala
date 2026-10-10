package net.systemvi.mainmenu.server.persistance.models

/**
 * Lifecycle state of a [[User]].
 *
 * Modelled as a Scala enum over an integer id, so a user stores a single
 * `status` column that a doobie `Read` maps back to the case object.
 */
sealed trait UserStatus {
  def id: Int
  def name: String
}

object UserStatus {
  /** A customer that has not signed in yet. */
  case object Anonymous extends UserStatus {
    override val id: Int = 0
    override val name: String = "ANONYMOUS"
  }

  /** A user with a full account (all staff, and signed-in customers). */
  case object Active extends UserStatus {
    override val id: Int = 1
    override val name: String = "ACTIVE"
  }

  val all: List[UserStatus] = List(Anonymous, Active)

  private val byId: Map[Int, UserStatus] = all.map(status => status.id -> status).toMap

  def fromId(id: Int): Option[UserStatus] = byId.get(id)

  def unsafeFromId(id: Int): UserStatus =
    fromId(id).getOrElse(throw new IllegalArgumentException(s"Unknown user status id: $id"))
}

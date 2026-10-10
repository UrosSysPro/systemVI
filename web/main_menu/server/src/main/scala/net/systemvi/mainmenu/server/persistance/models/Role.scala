package net.systemvi.mainmenu.server.persistance.models

/**
 * The three application roles.
 *
 * Modelled as a Scala enum over an integer id, so a user stores a single
 * `roleId` column that a doobie `Read` maps straight back to the case object.
 */
sealed trait Role {
  def id: Int
  def name: String
}

object Role {
  case object Admin extends Role {
    override val id: Int = 1
    override val name: String = "ADMIN"
  }

  case object Waiter extends Role {
    override val id: Int = 2
    override val name: String = "WAITER"
  }

  case object Customer extends Role {
    override val id: Int = 3
    override val name: String = "CUSTOMER"
  }

  /** All roles, in id order. Seeded into the `Roles` table by its migration. */
  val all: List[Role] = List(Admin, Waiter, Customer)

  private val byId: Map[Int, Role] = all.map(role => role.id -> role).toMap

  def fromId(id: Int): Option[Role] = byId.get(id)

  def unsafeFromId(id: Int): Role =
    fromId(id).getOrElse(throw new IllegalArgumentException(s"Unknown role id: $id"))
}

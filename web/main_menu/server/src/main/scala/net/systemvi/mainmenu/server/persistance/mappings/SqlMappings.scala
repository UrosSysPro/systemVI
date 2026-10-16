package net.systemvi.mainmenu.server.persistance.mappings

import doobie.*
import doobie.implicits.*
import doobie.generic.auto.*
import net.systemvi.mainmenu.server.persistance.models.Item
import net.systemvi.mainmenu.server.persistance.models.Category
import net.systemvi.mainmenu.server.persistance.models.Restaurant
import net.systemvi.mainmenu.server.persistance.models.RestaurantTable
import net.systemvi.mainmenu.server.persistance.models.Role
import net.systemvi.mainmenu.server.persistance.models.AuthIdentity
import net.systemvi.mainmenu.server.persistance.models.AuthProvider
import net.systemvi.mainmenu.server.persistance.models.User
import net.systemvi.mainmenu.server.persistance.models.UserStatus

import java.util.UUID

object SqlMappings {
  given Read[UUID] = Read[String].map(UUID.fromString)
  given Write[UUID] = Write[String].contramap(_.toString)

  // `imageBlob` is deliberately absent from these tuples: it has its own
  // column and is read only by the dedicated blob methods on the contexts, so
  // a listing can never accidentally drag image bytes along.
  given Read[Item] =
    Read[(UUID, String, String, Option[String], BigDecimal, Option[UUID])].map { case (uuid, name, imageUrl, description, price, categoryId) =>
      Item(uuid, name, imageUrl, description, price, categoryId)
    }

  given Read[Category] =
    Read[(UUID, String, Option[String], String, Option[UUID], Option[UUID])].map { case (uuid, name, description, imageUrl, parentId, restaurantId) =>
      Category(uuid, name, description, imageUrl, parentId, restaurantId)
    }

  given Read[Restaurant] = Read[(UUID, String, Option[String], String, String)].map(Restaurant.apply)

  given Read[RestaurantTable] = Read[(UUID, String, String, UUID)].map(RestaurantTable.apply)

  // Enum-like domain types are stored as their integer id, so a single column
  // maps back to the matching case object.
  given Read[Role] = Read[Int].map(Role.unsafeFromId)
  given Write[Role] = Write[Int].contramap(_.id)

  given Read[UserStatus] = Read[Int].map(UserStatus.unsafeFromId)
  given Write[UserStatus] = Write[Int].contramap(_.id)

  given Read[AuthProvider] = Read[Int].map(AuthProvider.unsafeFromId)
  given Write[AuthProvider] = Write[Int].contramap(_.id)

  given Read[AuthIdentity] = Read[(UUID, UUID, AuthProvider, String, Option[String])].map(AuthIdentity.apply)

  given Read[User] = Read[(UUID, Role, UserStatus, Option[String], Option[String])].map(User.apply)
}

package net.systemvi.server.persistance.models

sealed class KeyboardProfile(val id:Int)

object Profile60 extends KeyboardProfile(1)
object Profile65 extends KeyboardProfile(2)
object Profile75 extends KeyboardProfile(3)
object Profile100 extends KeyboardProfile(4)
object ProfileCorne extends KeyboardProfile(5)
object ProfileDactyl extends KeyboardProfile(6)
object Profile40 extends KeyboardProfile(7)
object ProfileTKL extends KeyboardProfile(8)

object KeyboardProfile {
  val values: List[KeyboardProfile] = List(
    Profile60,
    Profile65,
    Profile75,
    Profile100,
    ProfileCorne,
    ProfileDactyl,
    Profile40,
    ProfileTKL,
  )
}

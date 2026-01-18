package net.systemvi.server.persistance.models

sealed class KeyboardProfile(
                              val id: Int,
                              val name: String,
                            )

object Profile60      extends KeyboardProfile(1,"60%")
object Profile65      extends KeyboardProfile(2,"65%")
object Profile75      extends KeyboardProfile(3,"75%")
object Profile100     extends KeyboardProfile(4,"100%")
object ProfileCorne   extends KeyboardProfile(5,"Corne")
object ProfileDactyl  extends KeyboardProfile(6,"Dactyl")
object Profile40      extends KeyboardProfile(7,"40%")
object ProfileTKL     extends KeyboardProfile(8,"TKL")

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

package net.systemvi.server.persistance.models

sealed class KeyboardProfile(val id:Int)

object Profile60 extends KeyboardProfile(1)
object Profile65 extends KeyboardProfile(2)
object Profile75 extends KeyboardProfile(3)
object Profile100 extends KeyboardProfile(4)
object ProfileCorne extends KeyboardProfile(5)
object ProfileDactyl extends KeyboardProfile(6)

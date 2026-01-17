package net.systemvi.server.persistance.models

sealed class SwitchType(val id:Int)

object Clicky extends SwitchType(1)

object Tactile extends SwitchType(2)

object Linear extends SwitchType(3)


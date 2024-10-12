rootProject.name = "systemVI"

include ("voxel-world")
include ("examples")
include ("engine")

val subprojects=file("examples").listFiles()
    ?.filter { it.isDirectory && !listOf("src","assets","build").contains(it.name) }
    ?.map{ it.name }


subprojects?.forEach { include("examples:${it}") }
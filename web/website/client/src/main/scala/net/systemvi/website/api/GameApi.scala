package net.systemvi.website.api

import net.systemvi.common.model.{Game, ProductSpec}

private val games=List[Game](
  Game(1,"Atari Break Out","break_out",List(
    ProductSpec("spec 1","value 1"),
    ProductSpec("spec 1","value 1"),
    ProductSpec("spec 1","value 1"),
    ProductSpec("spec 1","value 1"),
    ProductSpec("spec 1","value 1"),
    ProductSpec("spec 1","value 1"),
  ),List("/images/games/breakout.png")),
  Game(2,"Snake","snake",List(),List("/images/games/snake.png")),
  Game(3,"Voxels","voxels",List(),List(
    "/images/games/voxels1.png",
    "/images/games/voxels2.png",
    "/images/games/voxels3.png",
    "/images/games/voxels4.png",
  )),
  Game(4,"Flappy Bird","flappy_bird",List(),List("/images/games/flappy-bird.png")),
)

private val defaultGame=Game(-1,"Default Game","default_code_name",List(),List("aaa"))

object GameApi {
  def all():List[Game]=games
  def get(id:Int):Game=games.find(_.id==id).getOrElse(defaultGame)
}


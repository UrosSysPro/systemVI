package com.systemvi.snake

import com.systemvi.engine.ui.Widget
import com.systemvi.engine.ui.utils.context.BuildContext
import com.systemvi.engine.ui.widgets.cupertino.Switch
import com.systemvi.engine.ui.widgets.{Column, Expanded, Row, SizedBox, State, StatefulWidget}

import scala.util.Random

class Game extends StatefulWidget{
  override def createState(): State = GameState()
}

case class Part(x:Int,y:Int)
case class GridSize(width:Int,height:Int)

class GameState extends State{

  val gridSize=GridSize(10,20)
  val random=Random()
  var food:Part=Part(0,0)
  var parts:List[Part]=List.empty

  override def build(context: BuildContext): Widget = {

    Row(
      children = List.range(0,gridSize.width).map{i=>
        Expanded(
          Column(
            children = List.range(0,gridSize.height).map{j=>
              val selected=parts.find(p=>p.x==i&&p.y==j) match {
                case Some(_)=>true
                case None =>false
              }
              Expanded(
                Switch(value = selected, onChange = value=>{})
              )
            }.toArray
          )
        )
      }.toArray
    )
  }
}

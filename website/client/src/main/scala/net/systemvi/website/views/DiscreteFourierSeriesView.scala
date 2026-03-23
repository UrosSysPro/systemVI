package net.systemvi.website.views

import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom.{CanvasRenderingContext2D, HTMLCanvasElement, console}
import org.scalajs.dom
import io.circe.scalajs.given
import io.circe.generic.auto.*
import io.circe.generic.*

case class MouseState(var x:Int,var y:Int,var down:Boolean)
case class Point(x: Int,y: Int)
def distance(p1:Point,p2:Point) = Math.sqrt((p1.x-p2.x)*(p1.x-p2.x)+(p1.y-p2.y)*(p1.y-p2.y)).toInt
def square(center:Point,size: Int): List[Point] = {
  val c=Point(center.x-size/2,center.y-size/2)
  (0 until 100).toList.map { i =>
    val p = i / 100f
    Point(c.x+(size * p).toInt, c.y)
  } ++ (0 until 100).toList.map { i =>
    val p = i / 100f
    Point(c.x+size,c.y+(size*p).toInt)
  }++ (0 until 100).toList.map { i =>
    val p = i / 100f
    Point(c.x+(size*(1f-p)).toInt,c.y+size)
  }++ (0 until 100).toList.map { i =>
    val p = i / 100f
    Point(c.x,c.y+(size*(1f-p)).toInt)
  }
}

val backgroundColor = "#515151"
var canvas: HTMLCanvasElement = null
var context: CanvasRenderingContext2D = null
var mouse = MouseState(0,0,false)
val canvasWidth = 1000
val canvasHeight = 1000
var points = square(Point(canvasWidth/2,canvasHeight/2),500)

private def setupCanvas():Unit = {
  canvas.width = canvasWidth
  canvas.height = canvasHeight

//  context.fillStyle = backgroundColor
//  context.fillRect(0,0,500,500)
}

private def loop(timestamp: Double): Unit = {
  //clear
  context.fillStyle = "rgba(51,51,51,0.1)"
  context.rect(0,0,canvas.width,canvas.height)
  context.fill()

  //draw points
  context.strokeStyle = "black"
  context.lineWidth = 2
  context.lineCap = "round"

  for(i<-points.indices.dropRight(1)){
    val p1 = points(i)
    val p2 = points(i+1)
    if(distance(p1,p2)>20){
      context.beginPath()
      context.rect(p1.x, p1.y,2,2)
      context.fill()
    }else{
      context.beginPath()
      context.moveTo(p1.x, p1.y)
      context.lineTo(p2.x, p2.y)
      context.stroke()
    }
  }
//  context.stroke()

//  points = List.empty

  //loop
  dom.window.requestAnimationFrame(loop _)
}

def discreteFourierSeriesView(): HtmlElement =  {
  div(
    width.percent(100),
    height.vh(100),
    display.flex,
    alignItems.center,
    justifyContent.center,
    canvasTag(
      width.px(canvasWidth),
      height.px(canvasHeight),
      onMouseDown --> {event =>
        mouse.down = true
      },
      onMouseUp --> {event =>
        mouse.down = false
      },
      onMouseMove --> {event =>
        val x = event.clientX - canvas.getBoundingClientRect().x
        val y = event.clientY - canvas.getBoundingClientRect().y
        if(mouse.down){
          points :+= Point(x.toInt,y.toInt)
          console.log(points.asJsAny)
        }
//        dom.console.log(x,y)
      },
      onMountCallback{ mountContext =>
        canvas = mountContext.thisNode.ref
        context = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
        setupCanvas()
        dom.window.requestAnimationFrame(loop _)
      }
    )
  )
}

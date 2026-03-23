package net.systemvi.website.views

import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom.{CanvasRenderingContext2D, HTMLCanvasElement, console}
import org.scalajs.dom
import io.circe.scalajs.given
import io.circe.generic.auto.*
import io.circe.generic.*

case class Complex(re: Double, im: Double):
  def +(o: Complex) = Complex(re + o.re, im + o.im)
  def *(o: Complex) = Complex(re * o.re - im * o.im, re * o.im + im * o.re)
  def magnitude: Double = Math.sqrt(re * re + im * im)
  def phase: Double = Math.atan2(im, re)

object Complex:
  def polar(r: Double, theta: Double) = Complex(r * Math.cos(theta), r * Math.sin(theta))

// One epicycle / Fourier coefficient
case class Epicycle(freq: Int, amplitude: Double, phase: Double)

// Compute DFT → list of epicycles, sorted by amplitude descending
def dft(points: List[Point]): List[Epicycle] =
  val N = points.length
  val phasors = points.map{case Point(x, y) => Complex(x, y)}

  (0 until N).map { k =>
    val sum = phasors.zipWithIndex.foldLeft(Complex(0, 0)) { case (acc, (p, n)) =>
      acc + p * Complex.polar(1.0, -2 * Math.PI * k * n / N)
    }
    val coeff = Complex(sum.re / N, sum.im / N)
    Epicycle(freq = k, amplitude = coeff.magnitude, phase = coeff.phase)
  }.toList.sortBy(-_.amplitude)  // biggest circles first (optional, looks nicer)

// Reconstruct point at t ∈ [0, 1)
def fourierPoint(epicycles: List[Epicycle], t: Double, count:Int = epicycles.length): Point =
  val N = epicycles.length
  val sum = epicycles.take(count).foldLeft(Complex(0, 0)) { case (acc, ep) =>
    acc + Complex.polar(ep.amplitude, ep.phase + 2 * Math.PI * ep.freq * t)
  }
  Point(sum.re, sum.im)

case class MouseState(var x:Int,var y:Int,var down:Boolean)
case class Point(x: Double,y: Double)
def distance(p1:Point,p2:Point) = Math.sqrt((p1.x-p2.x)*(p1.x-p2.x)+(p1.y-p2.y)*(p1.y-p2.y)).toInt
def square(center:Point,size: Int): List[Point] = {
  val c=Point(center.x-size/2,center.y-size/2)
  val pointsPerSide = 400
  (0 until pointsPerSide).toList.map { i =>
    val p = i / pointsPerSide.toFloat
    Point(c.x+(size * p).toInt, c.y)
  } ++ (0 until pointsPerSide).toList.map { i =>
    val p = i / pointsPerSide.toFloat
    Point(c.x+size,c.y+(size*p).toInt)
  }++ (0 until pointsPerSide).toList.map { i =>
    val p = i / pointsPerSide.toFloat
    Point(c.x+(size*(1f-p)).toInt,c.y+size)
  }++ (0 until pointsPerSide).toList.map { i =>
    val p = i / pointsPerSide.toFloat
    Point(c.x,c.y+(size*(1f-p)).toInt)
  }
}

val backgroundColor = "#515151"
var canvas: HTMLCanvasElement = null
var context: CanvasRenderingContext2D = null

var foregroundCanvas: HTMLCanvasElement = null
var foregroundContext: CanvasRenderingContext2D = null

var mouse = MouseState(0,0,false)
val canvasWidth = 1000
val canvasHeight = 1000
//var points = square(Point(canvasWidth/2,canvasHeight/2),500)
var points = List.empty[Point]
var epicycles = dft(points)
var t = 0d

private def setupCanvas():Unit = {
  canvas.width = canvasWidth
  canvas.height = canvasHeight

  foregroundCanvas.width = canvasWidth
  foregroundCanvas.height = canvasHeight
}

private def loop(timestamp: Double): Unit = {

//  val samples = Math.min(200,epicycles.length)
  val samples = epicycles.length

  foregroundContext.translate(canvas.width / 2, canvas.height / 2)
  val p = fourierPoint(epicycles,t,samples)
  foregroundContext.fillStyle="#0e7"
  foregroundContext.beginPath()
  foregroundContext.rect(p.x, p.y,2,2)
  foregroundContext.fill()
  t+=1d/epicycles.length
  foregroundContext.translate(-canvas.width / 2, -canvas.height / 2)

  //clear
  context.fillStyle = "rgba(51,51,51,1)"
  context.lineCap = "round"
  context.rect(0,0,canvas.width,canvas.height)
  context.fill()
  context.translate(canvas.width / 2, canvas.height / 2)
  var x = 0d
  var y = 0d
  context.lineWidth = 1
  for(e<-epicycles.take(samples)){
    context.beginPath()
    context.arc(x,y,e.amplitude,0,Math.PI*2)
    context.closePath()
    context.strokeStyle = "#222"
    context.stroke()
    val angle = t * e.freq * Math.PI * 2 + e.phase
    val nx = x+Math.cos(angle)*e.amplitude
    val ny = y+Math.sin(angle)*e.amplitude
    context.beginPath()
    context.moveTo(x,y)
    context.lineTo(nx,ny)
    context.strokeStyle = "#ccc"
    context.stroke()
    x=nx
    y=ny
  }
  context.translate(-canvas.width / 2, -canvas.height / 2)
  //draw points
//  context.strokeStyle = "black"
//  context.lineWidth = 2
//  context.lineCap = "round"
//
//  for(i<-points.indices.dropRight(1)){
//    val p1 = points(i)
//    val p2 = points(i+1)
//    if(distance(p1,p2)>20){
//      context.beginPath()
//      context.rect(p1.x, p1.y,2,2)
//      context.fill()
//    }else{
//      context.beginPath()
//      context.moveTo(p1.x, p1.y)
//      context.lineTo(p2.x, p2.y)
//      context.stroke()
//    }
//  }
//  context.stroke()

//  points = List.empty

  //loop
  dom.window.requestAnimationFrame(loop _)
}

def discreteFourierSeriesView(): HtmlElement =  {
  div(
    position.relative,
    background("rgba(51,51,51,1)"),
    overflow("hidden"),
    width.percent(100),
    height.vh(100),
    display.flex,
    alignItems.center,
    justifyContent.center,
    canvasTag(
      position.absolute,
      top.percent(50),
      left.percent(50),
      zIndex(2),
      transform("translate(-50%,-50%)"),
      position.absolute,
      width.px(canvasWidth),
      height.px(canvasHeight),
      onMouseDown --> {event =>
        mouse.down = true
      },
      onMouseUp --> {event =>
        mouse.down = false
        epicycles = dft(points)
        context.clearRect(0, 0, canvas.width, canvas.height)
        foregroundContext.clearRect(0, 0, canvas.width, canvas.height)
        t = 0
      },
      onMouseMove --> {event =>
        val x = event.clientX - canvas.getBoundingClientRect().x - canvasWidth/2
        val y = event.clientY - canvas.getBoundingClientRect().y - canvasHeight/2
        if(mouse.down){
          lazy val d = distance(points.last,Point(x,y))
          if(points.nonEmpty && d > 2 && d < 20){
            val last = points.last
            for(i<-0 until d){
              val a = i.toDouble/d
              points :+= Point(last.x*(1f-a)+x*a,last.y*(1f-a)+y*a)
            }
          }else{
            points :+= Point(x, y)
          }
        }
      },
      onMountCallback{ mountContext =>
        foregroundCanvas = mountContext.thisNode.ref
        foregroundContext = foregroundCanvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
      }
    ),
    canvasTag(
      position.absolute,
      top.percent(50),
      left.percent(50),
      transform("translate(-50%,-50%)"),
      width.px(canvasWidth),
      height.px(canvasHeight),
      onMountCallback{ mountContext =>
        canvas = mountContext.thisNode.ref
        context = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
        setupCanvas()
        dom.window.requestAnimationFrame(loop _)
      }
    )
  )
}

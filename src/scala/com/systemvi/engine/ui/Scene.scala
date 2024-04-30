
package com.systemvi.engine.ui

import com.systemvi.engine.application.Game
import com.systemvi.engine.camera.Camera3
import com.systemvi.engine.ui.utils.context.{BuildContext, DrawContext}
import com.systemvi.engine.ui.utils.data.Colors
import com.systemvi.engine.ui.utils.font.Font
import com.systemvi.engine.ui.utils.tree.{Animator, EventListenerFinder, TreeBuilder}
import com.systemvi.engine.ui.widgets.{GestureDetector, State}
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.utils.Utils.Buffer
import com.systemvi.engine.window.{InputProcessor, Window}
import org.joml.{Matrix4f, Vector2f}

class Scene(val root:Widget,initialWidth:Float=800,initialHeight:Float=600,font:Font) extends InputProcessor{
  //screen info
  var width: Int =initialWidth.toInt
  var height: Int =initialHeight.toInt
  //Scene state
  val states:Map[String, State]=Map[String,State]()
  val renderer:WidgetRenderer2=new WidgetRenderer2(
    Camera3.builder2d()
      .size(initialWidth,initialHeight)
      .scale(1,-1)
      .position(initialWidth/2,initialHeight/2)
      .build(),
    Font.load(
      "assets/examples/widgetRenderer2Test/font.PNG",
      "assets/examples/widgetRenderer2Test/font.json"
    )
  )
  //event listeners
  var focused:GestureDetector=null
  val mouse=new Vector2f()
  //three utils
  val eventListenerFinder=new EventListenerFinder()
  val threeBuilder=new TreeBuilder(states)
  val context=new BuildContext()
  val drawContext=DrawContext(renderer,new Matrix4f().identity())
  val animator:Animator=new Animator()
  //initial build
  threeBuilder.build(root,s"/${root.getClass.getSimpleName}",context)

  resize(initialWidth.toInt,initialHeight.toInt)
  def resize(width:Int,height:Int): Boolean = {
    this.width=width
    this.height=height
    root.calculateSize(new Vector2f(width,height))
    root.calculatePosition(new Vector2f(0,0))
    renderer.camera.orthographic(-width/2,width/2,-height/2,height/2,0,100)
    renderer.camera.position(width/2,height/2,0)
    renderer.camera.update()
    true
  }

  def animate(delta:Float): Unit = {
    animator.animate(root,delta)
  }
  def draw():Unit={
    drawContext.transform.identity()
    Utils.enableBlending()
    root.draw(drawContext)
    renderer.flush()
    Utils.disableBlending()
  }

  override def keyDown(key: Int, scancode: Int, mods: Int): Boolean = {
    if(focused!=null)return focused.keyDown(key,scancode,mods)
    false
  }
  override def keyUp(key: Int, scancode: Int, mods: Int): Boolean = {
    if(focused!=null)return focused.keyUp(key,scancode,mods)
    false
  }
  override def mouseDown(button: Int, mods: Int, x: Double, y: Double): Boolean = {
    mouse.set(x,y)
    var eventAccepted=false
    val stack=eventListenerFinder.find(root,mouse)
    while(!eventAccepted&&stack.nonEmpty){
      val detector=stack.pop()
      if(detector.mouseDown(button,mods,x-detector.position.x,y-detector.position.y)) {
        if(detector.focusable)focused=detector
        eventAccepted=true
      }
    }
    if(!eventAccepted)focused=null
    eventAccepted
  }
  override def mouseUp(button: Int, mods: Int, x: Double, y: Double): Boolean = {
    if(focused!=null)return focused.mouseUp(button,mods,x-focused.position.x,y-focused.position.y)
    false
  }
  override def mouseMove(x: Double, y: Double): Boolean = {
    mouse.set(x,y)
    //mouse enter
    var stack=eventListenerFinder.find(root)
    while (stack.nonEmpty){
      val widget=stack.pop()
      if(!widget.mouseOver&&widget.contains(mouse.x,mouse.y)){
        widget.mouseOver=true
        widget.mouseEnter()
      }
    }
    //mouse leave
    stack=eventListenerFinder.find(root)
    while (stack.nonEmpty){
      val widget=stack.pop()
      if(widget.mouseOver&& !widget.contains(mouse.x,mouse.y)){
        widget.mouseOver=false
        widget.mouseLeave()
      }
    }
    //mouse move
    var eventAccepted=false
    stack=eventListenerFinder.find(root,mouse)
    while(!eventAccepted&&stack.nonEmpty){
      val detector=stack.pop()
      if(detector.mouseMove(x-detector.position.x,y-detector.position.y))eventAccepted=true
    }
    eventAccepted
  }
  override def scroll(offsetX: Double, offsetY: Double): Boolean = {
    var eventAccepted=false
    val stack=eventListenerFinder.find(root,mouse)
    while(!eventAccepted&&stack.nonEmpty){
      val detector=stack.pop()
      if(detector.scroll(offsetX,offsetY))eventAccepted=true
    }
    eventAccepted
  }
}

object Scene{
  def apply(root: Widget, initialWidth:Float,initialHeight:Float,font:Font): Scene = {
    new Scene(root, initialWidth, initialHeight, font)
  }
}

object UIApplication{
  var font:Font=null
}
class UIApplication(title:String,home:Widget) extends Game(3,3,60,800,600,title){
  var scene:Scene=null
  override def setup(window: Window): Unit = {
    UIApplication.font=Font.load(
      "assets/examples/widgetRenderer2Test/font.PNG",
      "assets/examples/widgetRenderer2Test/font.json"
    )
    scene=new Scene(
      initialWidth = window.getWidth,
      initialHeight = window.getHeight,
      root = home,
      font = UIApplication.font
    )
    setInputProcessor(scene)
  }
  override def loop(delta: Float): Unit = {
    Utils.clear(0,0,0,0,Buffer.COLOR_BUFFER)
    scene.animate(delta)
    scene.resize(scene.width,scene.height)
    scene.draw()
  }

  override def resize(width: Int, height: Int): Boolean = scene.resize(width, height)
}

object runApp{
  def apply(title:String,home:Widget): Unit = {

    new UIApplication(title,home).run()
  }
}
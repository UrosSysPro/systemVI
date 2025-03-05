//>using jar ../engine.jar
//>using sourceJar ../engine.jar

import com.systemvi.engine.application.Game
import com.systemvi.engine.window.Window


object Main extends Game(3,3,60,800,600,"Ray Marching"){
  
  override def setup(window:Window): Unit = {
    
  }
  
  override def loop(delta:Float): Unit = {
    
  }
  
  def main(args:Array[String]): Unit = {
    run()    
  }
}

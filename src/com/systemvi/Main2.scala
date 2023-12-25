package com.systemvi

import com.systemvi.examples.polygons.Polygon2
import org.joml.Vector2f

case class User(var name:String="Name",var password:String="Password")
case class AdminUser(n:String,p:String) extends User(name = n, password = p);

object Main2{
  def main(args: Array[String]): Unit = {
    var p=Polygon2(Array(
      new Vector2f(0,0),
      new Vector2f(1,0),
      new Vector2f(0,1)
    ))
    p.points=Array();
    var a:Any=User(
      password="password",
      name="name"
    )
    a match {
      case 1=>print("a is int 1")
      case s:String=> print(s"a is type String and value is $a")
      case admin:AdminUser => print(s"user is admin ${admin.name}")
      case User(name,password)=> print(s"user name is ${name} and password ${password}")
      case a>2=>print("a je vece od 2")
      case _=>print("default")
    }
    a match {
      case null=>{}
      case a:String=>{}
      case _=>{}
    }
    var x:Int=1
    var y:Int=2
    var max=if(x>y)x else y
  }
}
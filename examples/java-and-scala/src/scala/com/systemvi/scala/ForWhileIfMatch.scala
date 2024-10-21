package com.systemvi.scala

object ForWhileIfMatch {
  def main(args: Array[String]): Unit = {
    val a=2
    val b=3

    //svaki blok koda u skali ima vrednost, vrednost je poslednja linija bloka koda    
    println({
      a+b
    })
    
    //svaki if u skali ima vrednost, vrednost je poslednja linija true bloka ili poslednja linija false bloka
    val max=if(a>b) a else b
    
    var i=0
    while(i<100){
      println(i)
      i+=1
    }
    
    // for moze da ima yield, sto je kao return, i rezultat fora je IndexedSeq[T] sto je kao niz tipa T
    val array=for( i <- 0 until 100)yield i
    
    //normalan for prolazi kroz niz, ili Seq[T] i poziva se po jednom za svaki clan niza
    for(i<-array)println(i)
    
    case class Point(x:Int,y:Int)
    
    val point=Point(x=2,y=23)
    
    //match je kao switch, samo sto moze da uporedjuje, matchuje po tipu, matchuje po vrednosti, dekonstruise tip, hvata greske...
    val message= point match {
      case Point(x,y) if x>y => "jeste tipa point i x je vece"
      case Point(x,y) if x>y => "jeste tipa point i y je vece"
      case p:Point if p.x==p.y => "jeste tipa point i x i y su jednaki"
      case null => "point je null"
      case _ => "default"
    }
  }
}

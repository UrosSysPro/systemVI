package com.systemvi.scala

class GenericType[T,V](val value:T,val value2:V) {

}

class GenericMethod{
  
  //cela klasa nije generic ali samo ova metoda jeste
  def f[V](a:V):V={
    a
  }
}


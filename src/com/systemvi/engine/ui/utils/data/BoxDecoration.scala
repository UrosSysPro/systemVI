package com.systemvi.engine.ui.utils.data

import org.joml.{Vector2f, Vector4f}

case class BoxDecoration(
                          color:Vector4f=new Vector4f(0),
                          borderRadius:Float=0,
                          border:Border=Border(),
                          boxShadow:Array[BoxShadow]=Array()
                        )
case class Border(
                   color:Vector4f=new Vector4f(0,0,0,1),
                   width:Float=2
                 )
case class BoxShadow(
                      offset:Vector2f=new Vector2f(0,0),
                      size:Float=0,
                      blur:Float=0,
                      color:Vector4f=new Vector4f(0f,0f,0f,1f)
                    )

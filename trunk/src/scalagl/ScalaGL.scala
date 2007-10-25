package scalagl

import javax.media.opengl._
import com.sun.opengl.util._

/**
 * Uitilities for OpenGL operations. Every method takes an additional 
 * implicit GL context which is used to issue operations. This way, it 
 * plays nicely with our GLListeners and without an implicit conversion.
 */
object ScalaGL {
  
  def localMatrix(block: =>Unit)(implicit gl: GL) {
    gl.glPushMatrix(); block; gl.glPopMatrix()
  }
  
  def newList(mode: Int)(block: =>Unit)(implicit gl: GL): Int = {
    val list = gl.glGenLists(1)
    gl.glNewList(list, mode); block; gl.glEndList()
    list
  }

  def newList(block: =>Unit)(implicit gl: GL): Int = 
    newList(GL.GL_COMPILE){ block }
  
  def primitive(prim: Int)(block: =>Unit)(implicit gl: GL) {
    gl.glBegin(prim); block; gl.glEnd
  }
  
  def enableAll(flags: Int*)(implicit gl: GL) { for(f <- flags) gl.glEnable(f) }
}

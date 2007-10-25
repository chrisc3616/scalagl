package scalagl

import javax.media.opengl._

/** 
 * Method signatures are rather heavyweight, but JOGL API contract 
 * is not to rely on GL object identity. So we prefetch it from 
 * the drawable and adapt the listener interface to this one. GL 
 * is implicit, so we can use the ScalaGL utility object without 
 * additional effort.
 *
 * We could wrap GL contexts to provide object identity and make 
 * signatures simpler, but this would be a lot more work. 
 */
trait GLListener {
  def init(drawable: GLAutoDrawable)(implicit gl: GL)
  def reshape(drawable: GLAutoDrawable, 
      x: Int, y: Int, width: Int, height: Int)(implicit gl: GL)
  def display(drawable: GLAutoDrawable)(implicit gl: GL)
  
  def displayChanged(drawable: GLAutoDrawable, modeChanged: Boolean, 
      deviceChanged: Boolean)(implicit gl: GL) {}
}

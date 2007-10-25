package scalagl

import java.awt.Frame
import java.awt.event._
import javax.media.opengl._
import com.sun.opengl.util._

abstract class SimpleGLApplication extends Application with GLListener {
  def title: String
  
  val frame = new Frame(title)
  val canvas = new javax.media.opengl.GLCanvas

  canvas.addGLEventListener(Utils.unwrapGLListener(this))
  frame.add(canvas)
  frame.setSize(300, 300)
  val animator = new Animator(canvas)
  frame.addWindowListener(new WindowAdapter {
    override def windowClosing(e: WindowEvent) {
      Utils.runOnThread { animator.stop(); System.exit(0) }
    }
  })
  frame.show()
  animator.start()
}

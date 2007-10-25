package scalagl

import javax.media.opengl._
import java.util._

object Utils {
  def Runnable(block: => Unit) = new Runnable { def run { block } }
  def runOnThread(block: => Unit) { new Thread(Runnable{block}).start() }
  def every(msecs: Long)(block: =>Unit) {
    new Timer().schedule(new TimerTask { def run { block } }, 0, msecs)
  }
  
  def unwrapGLListener(l: GLListener) = new GLEventListener {
    def init(drawable: GLAutoDrawable) { l.init(drawable)(drawable.getGL) }
    def reshape(drawable: GLAutoDrawable, x: Int, y: Int, w: Int, h: Int) { 
      l.reshape(drawable, x, y, w, h)(drawable.getGL)
    }
    def display(drawable: GLAutoDrawable) { l.display(drawable)(drawable.getGL) }
    def displayChanged(drawable: GLAutoDrawable,
        modeChanged: Boolean, deviceChanged: Boolean) {
      l.displayChanged(drawable, modeChanged, deviceChanged)(drawable.getGL)
    }
  }
  
}

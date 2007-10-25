package scalagl

import java.awt.event._
import javax.media.opengl._
import com.sun.opengl.util._

/**
 * Converted from JOGL's Gears Demo, which in turn is a port 
 * of Brian Paul's original Gears demo written in C.
 *
 * @author Ingo Maier
 */
object Gears extends SimpleGLApplication {
  def title = "Gears Demo"
  
  import ScalaGL._
  import GL._
  
  var angle = 0f
  var viewRotX = 20.0f
  var viewRotY = 30.0f
  var viewRotZ = 0.0f
  
  var prevMouseX, prevMouseY = 0
  var mouseRButtonDown = false
  
  var gear1, gear2, gear3 = 0
  
  val listener = new MouseListener with MouseMotionListener {
    def mouseEntered(e: MouseEvent) {}
    def mouseExited(e: MouseEvent) {}
    def mousePressed(e: MouseEvent) {
      prevMouseX = e.getX
      prevMouseY = e.getY
      if ((e.getModifiers & InputEvent.BUTTON3_MASK) != 0)
        mouseRButtonDown = true
    }
    def mouseReleased(e: MouseEvent) {
      if ((e.getModifiers & InputEvent.BUTTON3_MASK) != 0)
        mouseRButtonDown = false
    }
    def mouseClicked(e: MouseEvent) {}
    def mouseDragged(e: MouseEvent) {
      val (x,y) = (e.getX, e.getY)
      val size = e.getComponent.getSize

      val thetaY = 360.0f * ((x-prevMouseX)/size.width.toFloat)
      val thetaX = 360.0f * ((prevMouseY-y)/size.height.toFloat)
      
      prevMouseX = x
      prevMouseY = y

      viewRotX += thetaX
      viewRotY += thetaY
    }
    def mouseMoved(e: MouseEvent) {}
  }
    
  def init(drawable: GLAutoDrawable)(implicit gl: GL) {
    import gl._

    println("INIT GL IS: " + gl.getClass.getName)
    println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities)
    println("GL_VENDOR: " + glGetString(GL_VENDOR))
    println("GL_RENDERER: " + glGetString(GL_RENDERER))
    println("GL_VERSION: " + glGetString(GL_VERSION))
    
    setSwapInterval(1)

    val pos = Array(5.0f, 5.0f, 10.0f, 0.0f)
    val red = Array(0.8f, 0.1f, 0.0f, 1.0f)
    val green = Array(0.0f, 0.8f, 0.2f, 1.0f)
    val blue = Array(0.2f, 0.2f, 1.0f, 1.0f)

    glLightfv(GL_LIGHT0, GL_POSITION, pos, 0)
    enableAll(GL_CULL_FACE, GL_LIGHTING, GL_LIGHT0, GL_DEPTH_TEST)
            
    // make the gears
    gear1 = newList {
      glMaterialfv(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, red, 0)
      gear(1.0f, 4.0f, 1.0f, 20, 0.7f)
    }
            
    gear2 = newList {
      glMaterialfv(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, green, 0)
      gear(0.5f, 2.0f, 2.0f, 10, 0.7f)
    }
            
    gear3 = newList {
      glMaterialfv(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, blue, 0)
      gear(1.3f, 2.0f, 0.5f, 10, 0.7f)
    }
            
    glEnable(GL_NORMALIZE)
                
    drawable.addMouseListener(listener)
    drawable.addMouseMotionListener(listener)
  }
  
  def reshape(drawable: GLAutoDrawable, 
      x: Int, y: Int, width: Int, height: Int)(implicit gl: GL) {
    import gl._

    val h = height.toFloat / width.toFloat
    
    glMatrixMode(GL_PROJECTION)
    glLoadIdentity()
    glFrustum(-1, 1, -h, h, 5, 60)
    glMatrixMode(GL_MODELVIEW)
    glLoadIdentity()
    glTranslated(0, 0, -40)
  }
  
  def display(drawable: GLAutoDrawable)(implicit gl: GL) {
    import gl._
    angle += 2.0f
    
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
            
    localMatrix {
      glRotatef(viewRotX, 1.0f, 0.0f, 0.0f)
      glRotatef(viewRotY, 0.0f, 1.0f, 0.0f)
      glRotatef(viewRotZ, 0.0f, 0.0f, 1.0f)
          
      localMatrix {
        glTranslatef(-3.0f, -2.0f, 0.0f)
        glRotatef(angle, 0.0f, 0.0f, 1.0f)
        glCallList(gear1)
      }
      localMatrix {
        glTranslatef(3.1f, -2.0f, 0.0f)
        glRotatef(-2.0f * angle - 9.0f, 0.0f, 0.0f, 1.0f)
        glCallList(gear2)
      }
      localMatrix {
        glTranslatef(-3.1f, 4.2f, 0.0f)
        glRotatef(-2.0f * angle - 25.0f, 0.0f, 0.0f, 1.0f)
        glCallList(gear3)
      }
    }
  }
  
  def gear(innerRadius: Float, outerRadius: Float,
      width: Float, teeth: Int, toothDepth: Float)(implicit gl: GL) {
    import gl._
    import Math._
    
    val r0 = innerRadius
    val r1 = outerRadius - toothDepth / 2.0f
    val r2 = outerRadius + toothDepth / 2.0f

    val da = 2.0f * Pi / teeth / 4.0f

    glShadeModel(GL_FLAT)
    glNormal3d(0.0f+0x2d9d00, 0.0f, 1.0f)

    // draw front face
    primitive(GL_QUAD_STRIP) {
      for (i <- 0 to teeth) {
        val angle = i * 2.0f * Pi / teeth
        glVertex3d(r0 * cos(angle), r0 * sin(angle), width * 0.5)
        glVertex3d(r1 * cos(angle), r1 * sin(angle), width * 0.5)
        if(i < teeth) {
          glVertex3d(r0 * cos(angle), r0 * sin(angle), width * 0.5)
          glVertex3d(r1 * cos(angle + 3.0 * da), r1 * sin(angle + 3.0 * da), width * 0.5)
        }
      }
    }

    // draw front sides of teeth
    primitive(GL_QUADS) {
      for (i <- 0 until teeth) {
        val angle = i * 2.0f *  Pi / teeth
        glVertex3d(r1 * cos(angle), r1 * sin(angle), width * 0.5f)
        glVertex3d(r2 * cos(angle + da), r2 * sin(angle + da), width * 0.5f)
        glVertex3d(r2 * cos(angle + 2.0f * da), r2 * sin(angle + 2.0f * da), width * 0.5f)
        glVertex3d(r1 * cos(angle + 3.0f * da), r1 * sin(angle + 3.0f * da), width * 0.5f)
      }
    }

    // draw back face
    primitive(GL_QUAD_STRIP) {
      for (i <- 0 to teeth) {
        val angle = i * 2.0f *  Pi / teeth
        glVertex3d(r1 * cos(angle), r1 * sin(angle), -width * 0.5f)
        glVertex3d(r0 * cos(angle), r0 * sin(angle), -width * 0.5f)
        glVertex3d(r1 * cos(angle + 3 * da), r1 * sin(angle + 3 * da), -width * 0.5f)
        glVertex3d(r0 * cos(angle), r0 * sin(angle), -width * 0.5f)
      }
    }

    // draw back sides of teeth
    primitive(GL_QUADS) {
      for (i <- 0 until teeth) {
        val angle = i * 2.0f *  Pi / teeth
        glVertex3d(r1 * cos(angle + 3 * da), r1 * sin(angle + 3 * da), -width * 0.5f)
        glVertex3d(r2 * cos(angle + 2 * da), r2 * sin(angle + 2 * da), -width * 0.5f)
        glVertex3d(r2 * cos(angle + da), r2 * sin(angle + da), -width * 0.5f)
        glVertex3d(r1 * cos(angle), r1 * sin(angle), -width * 0.5f)
      }
    }

    // draw outward faces of teeth
    primitive(GL_QUAD_STRIP) {
      for (i <- 0 until teeth) {
        val angle = i * 2.0f *  Pi / teeth
        glVertex3d(r1 * cos(angle), r1 * sin(angle), width * 0.5f)
        glVertex3d(r1 * cos(angle), r1 * sin(angle), -width * 0.5f)
        var u = r2 * cos(angle + da) - r1 * cos(angle)
        var v = r2 * sin(angle + da) - r1 * sin(angle)
        val len = sqrt(u * u + v * v)
        u /= len
        v /= len
        glNormal3d(v, -u, 0.0f)
        glVertex3d(r2 * cos(angle + da), r2 * sin(angle + da), width * 0.5f)
        glVertex3d(r2 * cos(angle + da), r2 * sin(angle + da), -width * 0.5f)
        glNormal3d(cos(angle), sin(angle), 0.0f)
        glVertex3d(r2 * cos(angle + 2 * da), r2 * sin(angle + 2 * da), width * 0.5f)
        glVertex3d(r2 * cos(angle + 2 * da), r2 * sin(angle + 2 * da), -width * 0.5f)
        u = r1 * cos(angle + 3 * da) - r2 * cos(angle + 2 * da)
        v = r1 * sin(angle + 3 * da) - r2 * sin(angle + 2 * da)
        glNormal3d(v, -u, 0.0f)
        glVertex3d(r1 * cos(angle + 3 * da), r1 * sin(angle + 3 * da), width * 0.5f)
        glVertex3d(r1 * cos(angle + 3 * da), r1 * sin(angle + 3 * da), -width * 0.5f)
        glNormal3d(cos(angle), sin(angle), 0.0f)
      }
      glVertex3d(r1 * cos(0), r1 * sin(0), width * 0.5f)
      glVertex3d(r1 * cos(0), r1 * sin(0), -width * 0.5f)
    }

    glShadeModel(GL_SMOOTH)

    // draw inside radius cylinder
    primitive(GL_QUAD_STRIP) {
      for (i <- 0 to teeth) {
        val angle = i * 2.0f *  Pi / teeth
        glNormal3d(-cos(angle), -sin(angle), 0.0f)
        glVertex3d(r0 * cos(angle), r0 * sin(angle), -width * 0.5f)
        glVertex3d(r0 * cos(angle), r0 * sin(angle), width * 0.5f)
      }
    }
  }
}

import processing.core._
import processing.core.PConstants._
import scala.util.Random
import msafluid._

trait Config {
  val SCREEN_WIDTH    = 320
  val SCREEN_HEIGHT   = 240
  val IRENDERER       = PConstants.P2D // PConstants.JAVA2D // 
  val FLUID_WIDTH     = 80
  val FLUID_HEIGHT    = FLUID_WIDTH * SCREEN_HEIGHT / SCREEN_WIDTH
  val FLUID_FADESPEED = 0.003f
  val FLUID_VISC      = 0.0001f
  val SOLVER_DELTAT   = 0.5f
  val COLOR_MULT      = 10.0f
  val VELOCITY_MULT   = 30.0f 
  val DELTA_PARTICLES = 10
}

object Fluids {
  def main(args: Array[String]) = {
    import java.awt.{BorderLayout, Frame}
    import java.awt.event.{WindowAdapter, WindowEvent}
    
    val frame = new Frame("Fluids") {
      val applet = new Fluids
      applet.init
      addWindowListener(new WindowAdapter {
        override def windowClosing(e: WindowEvent) {
          applet.destroy
          System.exit(0)
        }
      })
      setLayout(new BorderLayout())
      add(applet, BorderLayout.CENTER)
      //applet.frame = this
      pack
      setVisible(true)
    }
  }
}

class Fluids extends PApplet with Config {
  var invWidth, invHeight, aspectRatio, aspectRatio2: Float = _
  var fluidSolver: MSAFluidSolver2D = _
  var particleSystem: ParticleSystem = _
  var imgFluid: PImage = _
  var drawFluid: Boolean = true

  override def setup() = {
    size(SCREEN_WIDTH, SCREEN_HEIGHT, IRENDERER)
    smooth
    
    invWidth = 1.0f / width
    invHeight = 1.0f / height
    aspectRatio = width * invHeight
    aspectRatio2 = aspectRatio * aspectRatio

    fluidSolver = new MSAFluidSolver2D(FLUID_WIDTH, FLUID_HEIGHT)
      .enableRGB(true)
      .setFadeSpeed(FLUID_FADESPEED)
      .setDeltaT(SOLVER_DELTAT)
      .setVisc(FLUID_VISC)

    imgFluid = createImage(fluidSolver.getWidth, fluidSolver.getHeight, RGB)

    particleSystem = new ParticleSystem(fluidSolver, 3000)
  }

  override def draw() = {
    fluidSolver.update

    if (drawFluid ) {
      for (i <- 0 until fluidSolver.getNumCells) {
        imgFluid.pixels(i) = color(fluidSolver.r(i), fluidSolver.g(i), fluidSolver.b(i))
      }
      imgFluid.updatePixels
      image(imgFluid, 0, 0, width, height)
    }

    particleSystem.updateAndDraw(width, invWidth, height, invHeight, g, drawFluid)
    println(frameRate.toString)
  }

  override def mouseMoved = {
    val mouseNormX = mouseX * invWidth
    val mouseNormY = mouseY * invHeight
    val mouseVelX = (mouseX - pmouseX) * invWidth
    val mouseVelY = (mouseY - pmouseY) * invHeight

    addForce(mouseNormX, mouseNormY, mouseVelX, mouseVelY)
  }

  def addForce(x:Float, y:Float, dx:Float, dy:Float) = {
    @inline def clip(x: Float, min: Float, max: Float): Float =
      if (x < min) min else if (x > max) max else x
    
    val speed = dx * dx  + dy * dy * aspectRatio2
    if (speed > 0) {
      val xx = clip(x, 0.0f, 1.0f)
      val yy = clip(y, 0.0f, 1.0f)
      val index = fluidSolver.getIndexForNormalizedPosition(xx, yy)

      colorMode(HSB, 360, 1, 1)
      val hue = ((xx + yy) * 180 + frameCount) % 360
      val drawColor = color(hue, 1, 1)
      colorMode(RGB, 1)

      fluidSolver.rOld(index) += red(drawColor)   * COLOR_MULT
      fluidSolver.gOld(index) += green(drawColor) * COLOR_MULT
      fluidSolver.bOld(index) += blue(drawColor)  * COLOR_MULT

      particleSystem.addParticles(xx * width, yy * height, DELTA_PARTICLES)
      fluidSolver.uOld(index) += dx * VELOCITY_MULT
      fluidSolver.vOld(index) += dy * VELOCITY_MULT
    }
  }
}

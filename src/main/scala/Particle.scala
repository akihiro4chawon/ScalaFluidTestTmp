import processing.core._
import processing.core.PApplet._

import scala.util.Random
import java.nio.FloatBuffer
import msafluid._

object Particle {
  val MOMENTUM = 0.5f
  val FLUID_FORCE = 0.6f
}

class Particle(fluidSolver:MSAFluidSolver2D) {
  import Particle.{MOMENTUM, FLUID_FORCE}
  var x, y, vx, vy, alpha, mass: Float = _

  def init(x: Float, y: Float) = {
    this.x = x
    this.y = y
    alpha = 0.3f + Random.nextFloat * 0.7f
    mass = 0.1f + Random.nextFloat * 0.9f
  }

  def update(width: Float, invWidth: Float, height: Float, invHeight: Float) {
    if (alpha == 0) return

    val fluidIndex = fluidSolver.getIndexForNormalizedPosition(x * invWidth, y * invHeight)
    vx = fluidSolver.u(fluidIndex) * width  * mass * FLUID_FORCE + vy * MOMENTUM
    vy = fluidSolver.v(fluidIndex) * height * mass * FLUID_FORCE + vy * MOMENTUM

    x += vx
    y += vy

    @inline
    def clipComponentWise(c: Float, min: Float, max: Float, ifOut: => Unit) =
      if      (c < min) {ifOut; min}
      else if (c > max) {ifOut; max}
      else     c
    x = clipComponentWise(x, 0.0f, width,  vx = -vx)
    y = clipComponentWise(y, 0.0f, height, vy = -vy)

    if (vx * vx + vy * vy < 1) {
      vx = Random.nextFloat * 2f - 1f
      vy = Random.nextFloat * 2f - 1f
    }

    alpha *= 0.999f
    if (alpha < 0.01) alpha = 0
  }

  def drawOldSchool(g: PGraphics) {
    g.stroke(1, 1, 1, alpha)
    g.line(x, y, x - vx, y - vy)
  }
}

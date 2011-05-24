import processing.core._
import scala.collection.mutable.ArrayBuffer
import scala.util.Random
import msafluid._

class ParticleSystem(fluidSolver: MSAFluidSolver2D, maxParticles: Int) {
  var curIndex: Int = 0
  val particles = Array.fill(maxParticles)(new Particle(fluidSolver))
  
  def updateAndDraw(width:Float, invWidth:Float, height:Float, invHeight:Float, g: PGraphics, drawFluid:Boolean) = {
    for (p <- particles) {
      if (p.alpha > 0) {
        p.update(width, invWidth, height, invHeight)
        p.drawOldSchool(g)
      }
    }
  }

  def addParticles(x: Float, y: Float, count: Int) {
    for (i <- 0 until count)
      addParticle(x + Random.nextInt(30) - 15, y + Random.nextInt(30) - 15)
  }

  def addParticle(x: Float, y: Float) {
    particles(curIndex).init(x, y)
    curIndex += 1
    if (curIndex >= particles.length) curIndex = 0
  }
}

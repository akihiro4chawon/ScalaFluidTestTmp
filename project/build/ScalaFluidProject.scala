import sbt._
import de.element34.sbteclipsify._

class ScalaAppletTestProject(info: ProjectInfo)
  extends DefaultProject(info)
  with ProguardProject
  with Eclipsify {
  
  override def mainClass = Some("Fluids")
  
  override def proguardOptions =
    keepAllProcessingCore :: proguardKeepMain(mainClass.get) :: super.proguardOptions
  private def keepAllProcessingCore = "-keep class processing.core.** { *; }"
  private def keepMainClass = """
    -keepclasseswithmembers public class *
    { public static void main(java.lang.String[]); }
  """

//  override def proguardDefaultArgs = "-dontwarn" :: "-dontoptimize" :: "-dontobfuscate" :: proguardOptions 
  override def proguardDefaultArgs = "-dontwarn" :: proguardOptions 

  override def proguardInJars =
    Path.fromFile(scalaLibraryJar) +++ super.proguardInJars
}


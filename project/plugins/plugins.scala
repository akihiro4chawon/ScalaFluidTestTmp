import sbt._

class Plugins(info: ProjectInfo) extends PluginDefinition(info) {
//  val ukMirrorRepo = "UK Maven Repository" at "http://uk.maven.org/maven2"
//  override def ivyRepositories =
//    Resolver.withDefaultResolvers(Seq(ukMirrorRepo), false, true) 

  lazy val eclipse = "de.element34" % "sbt-eclipsify" % "0.7.0"
  lazy val proguard = "org.scala-tools.sbt" % "sbt-proguard-plugin" % "0.0.5"
}

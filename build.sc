import $file.webpack
import ammonite.ops._
import mill._
import mill.scalajslib._
import mill.scalalib._
import mill.scalalib.scalafmt._
import webpack.ScalaJSWebpackModule

trait CommonScalaModule extends ScalaModule with ScalafmtModule {
  def scalaVersion: T[String] = "2.13.2"

  def scalaJSVersion: T[String] = "1.0.1"

  def osLibVersion = "0.6.2"

  def akkaHttpVersion = "10.1.12"

  def akkaStreamVersion = "2.6.5"

  def scalaJsDomVersion = "1.0.0"

  def uuidVersion = "8.1.0"
}

trait CommonScalaJsModule extends ScalaJSModule with CommonScalaModule {
  def platformSegment = "js"
}

object shared {

  object jvm extends CommonScalaModule {
    override def millSourcePath = super.millSourcePath / up
  }

  object js extends CommonScalaJsModule {
    override def millSourcePath = super.millSourcePath / up
  }
}

object frontend extends CommonScalaJsModule with ScalaJSWebpackModule {

  override def moduleDeps: Seq[ScalaJSModule] = Seq(shared.js)

  override def npmDeps = Agg("uuid" -> uuidVersion)

  override def ivyDeps = Agg(
    ivy"org.scala-js::scalajs-dom::$scalaJsDomVersion"
  )

}

object backend extends CommonScalaModule {

  override def moduleDeps = Seq(shared.jvm)

  override def resources = T.sources {
    super.resources() :+ frontend.fastOptWp()
  }

  override def ivyDeps = Agg(
    ivy"com.lihaoyi::os-lib:$osLibVersion",
    ivy"com.typesafe.akka::akka-http-xml:$akkaHttpVersion",
    ivy"com.typesafe.akka::akka-http2-support:$akkaHttpVersion",
    ivy"com.typesafe.akka::akka-stream:$akkaStreamVersion"
  )

  override def mainClass = Some("webapp.Server")
}
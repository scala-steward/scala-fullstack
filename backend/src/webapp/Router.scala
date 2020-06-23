package webapp

import akka.http.scaladsl.coding.Gzip
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.{Directives, Route}

object Router extends Directives {

  private def httpEntity(content: String): HttpEntity.Strict = HttpEntity(ContentTypes.`text/html(UTF-8)`, content)

  private val indexHtml = httpEntity(os.read(os.resource / "public" / "index.html"))
  private val webpackBundle = httpEntity(os.read(os.resource / "out-bundle.js"))
  private val webpackSourceMap = httpEntity(os.read(os.resource / "out-bundle.js.map"))

  val route: Route = encodeResponseWith(Gzip) {
    pathSingleSlash {
      complete(indexHtml)
    } ~ path("out-bundle.js") {
      complete(webpackBundle)
    } ~ path("out-bundle.js.map") {
      complete(webpackSourceMap)
    }
  }

}
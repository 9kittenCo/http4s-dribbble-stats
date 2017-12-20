import fs2.Task
import org.http4s.HttpService
import org.http4s.server.blaze.BlazeBuilder
import org.http4s.util.StreamApp
import services._

object Server extends StreamApp {
  import cats.implicits._

  val port: Int = Option(System.getProperty("http.port")).getOrElse("8080").toInt
  val host: String = Option(System.getProperty("http.host")).getOrElse("0.0.0.0") //

  val services: HttpService = HelloWorld.service |+| DribbbleService.service
  def stream(args: List[String]): fs2.Stream[Task, Nothing] = BlazeBuilder.bindHttp(port,host)
    .mountService(services, "/")
    .serve
  }
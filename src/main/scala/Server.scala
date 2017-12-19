import fs2.Task
import org.http4s.HttpService
import org.http4s.server.blaze.BlazeBuilder
import org.http4s.util.StreamApp
import services._

object Server extends StreamApp {
  import cats.implicits._

  val port: Int = Option(System.getProperty("http.port")).getOrElse("8080").toInt
  val host: String = Option(System.getProperty("http.host")).getOrElse("0.0.0.0") //dribbble-stats.local

  val services: HttpService = HelloWorld.service |+| DribbbleService.service
  def stream(args: List[String]): fs2.Stream[Task, Nothing] = BlazeBuilder.bindHttp(port,host)
    .mountService(services, "/")
    .serve
  }

//import cats.effect.IO
//import fs2.{Stream, Task}
//import io.circe._
//import io.circe.generic.auto._
//import models._
//import org.http4s._
//import org.http4s.client._
//import org.http4s.client.blaze._
//
//object Server {
//  def main(args: Array[String]): Unit = {
//    lazy val serviceEndpoint: String = "https://api.dribbble.com/v1"
//    lazy val serviceAccessToken: String = "2e098a5061db99c93952ddd371c464f15cced1ba62ecdb02b8e0bbcab1d22120"
// //   val client: Client[IO] = SimpleHttp1Client()
//    val client: Client = PooledHttp1Client()
//    val header: Header = Header("Authorization", s"Bearer $serviceAccessToken")
//
//    val listFollowers: Task[List[Follower]] =
//      request[List[Follower]](client, header, serviceEndpoint, s"users/alagoon/followers")
//
//
//
//    client.shutdownNow()
//  }
//
//  def request[T](client: Client, headers: Header, serviceEndpoint: String, path: String)(implicit decoder: Decoder[T]): Task[T] = {
//
//    val uri: Uri = Uri.unsafeFromString(s"$serviceEndpoint/$path")
//
//    val req = Task.now(
//      Request(headers = Headers(headers),
//        uri = uri))
//
//    client.expect[T](req)(jsonOf[T]).get
// //   client.expect[T](req)
//  }
//}

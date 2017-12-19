//package http4sdribbblestats
//
//import fs2.Task
//import org.http4s.UriTemplate._
//import org.http4s._
//import org.http4s.client.blaze.PooledHttp1Client
//import org.http4s.dsl._
//
//import scala.util.{Failure, Success}
//
//object Dribbble {
//
//  private lazy val serviceEndpoint: String = System.getProperty("api.dribbble.endpoint")
//  private lazy val serviceAccessToken: String = System.getProperty("api.dribbble.clientAccessToken")
//
//  val header = Header("Authorization", s"Bearer $serviceAccessToken")
//  val client = PooledHttp1Client()
//
//  val service = HttpService {
//    case GET -> Root / "top10" / name =>
//      val resp = mkRequest(s"/users/$name/followers")
//      Ok(resp)
//  }
//
//
//  def mkRequest(url: String): String = {
//    UriTemplate(path = List(PathElm(serviceEndpoint), PathElm(url))).toUriIfPossible match {
//    case Success(rUrl) =>
//    val req: Task[Request] = Task.now(
//      Request(headers = Headers(header),
//        uri = rUrl))
//    client.expect[String](req).unsafeRun().mkString
//    case Failure(text)=> new Exception(text).toString
//    }
//
////    val req: Task[Request] = Task.now(
////      Request(headers = Headers(header),
////        uri = requestUrl))
////    client.expect[String](req).unsafeRun().mkString
//  }
//
//  client.shutdownNow()
//}

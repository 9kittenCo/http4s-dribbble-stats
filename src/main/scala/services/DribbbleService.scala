package services

import java.util.Date
import java.util.concurrent.TimeUnit

import fs2.{Scheduler, Strategy, Task}
import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import models._
import org.http4s._
import org.http4s.circe._
import org.http4s.client._
import org.http4s.client.blaze.PooledHttp1Client
import org.http4s.dsl._
import org.http4s.util.CaseInsensitiveString

import scala.concurrent.duration.{FiniteDuration, _}

object DribbbleService {

  implicit val strategy: Strategy = Strategy.fromExecutionContext(scala.concurrent.ExecutionContext.Implicits.global)
  implicit val scheduler: Scheduler = fs2.Scheduler.fromFixedDaemonPool(4, threadName = "scheduler")

  lazy val serviceEndpoint: String = "https://api.dribbble.com/v1"
  lazy val serviceAccessToken: String = "2e098a5061db99c93952ddd371c464f15cced1ba62ecdb02b8e0bbcab1d22120"
  val client: Client = PooledHttp1Client()
  val header: Header = Header("Authorization", s"Bearer $serviceAccessToken")

  val service: HttpService = HttpService {
    case GET -> Root / "top10" :? NameQueryParamMatcher(name) =>
      Ok(getLikers(name).asJson)
  }

//

  object NameQueryParamMatcher extends QueryParamDecoderMatcher[String]("name")

  def getLikers(login: String): List[Liker] = {
    val maybeLikes: Task[Vector[Like]] = for {
      followers <- getFollowers(login)
      shots <- Task.parallelTraverse(followers.filter(_.follower.shots_count > 0))(follower =>
        getShots(follower.follower.username)).map(_.flatten)
      likes <- Task.parallelTraverse(shots.filter(_.likes_count > 0))(shot =>
        getLikes(shot)).map(_.flatten)
    } yield likes
    // TODO: Debug needed.
   val l =  (maybeLikes map { likes =>
      likes.groupBy(_.user.id) mapValues (userLikes =>
        Liker(userLikes.head.user, userLikes.length))
    } map (_.values.toList.sortBy(liker => -liker.like_count))).unsafeRun().take(10)
    debug(s"response: $l")
    l
  }

  def getLikes(shot: Shot): Task[List[Like]] = {
    debug("Task getLikes")
    request[List[Like]](s"/shots/${shot.id}/likes")
  }

  def getShots(username: String): Task[List[Shot]] = {
    debug("Task getShots")
    request[List[Shot]](s"/users/$username/shots")
  }

  def getFollowers(username: String): Task[List[Follower]] = {
    debug("Task getFollowers")
    request[List[Follower]](s"/users/$username/followers")
  }

  def request[T](path: String)(implicit decoder: Decoder[T]): Task[T] = {

    val uri: Uri = Uri.unsafeFromString(s"$serviceEndpoint/$path")

    val req = Task.now(
      Request(headers = Headers(header),
        uri = uri))

    def requestWithDelay(attempts: Double = 0.0, delay: FiniteDuration = 500 millis): Task[T] = {
      val max_attempts = 8
      debug(s"")
      client.fetch[T](req) { response =>
        response.status match {
          case Ok => response.as(jsonOf[T])
          case Status.TooManyRequests if attempts < max_attempts =>
            response.headers.get(CaseInsensitiveString("X-RateLimit-Reset")).map(_.value.toLong * 1000) match {
            case None =>
              debug(s"Task fail!")
              Task.fail(new Exception(s"Number of attempts exceeded: ${response.status} ${response.body}"))
            case Some(timestamp) =>
              val fd = FiniteDuration(timestamp - new Date().getTime, TimeUnit.MILLISECONDS)
              debug(s"Task start sleeping, $fd {$path}")
              Task.delay(fd)
              val t: Task[T] = requestWithDelay(attempts + 1)
              debug(s"Task awaking $t")
              t
          }
          case Status.TooManyRequests => Task.fail(new Exception("You are fail this city!"))
          case _ =>
            debug(s"Invalid response: ${response.status} ${response.body}")
            client.shutdownNow()
            Task.fail(new Exception(s"Invalid response: ${response.status} ${response.body}"))
        }
      }
    }

    requestWithDelay()
  }

  def debug(msg: String): Unit = {
    val now = java.time.format.DateTimeFormatter.ISO_INSTANT
      .format(java.time.Instant.now)
      .substring(11, 23)
    val thread = Thread.currentThread.getName
    println(s"$now [$thread] $msg")
  }

}

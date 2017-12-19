package models

case class Follower(
                     id: Long,
                     follower: User
                   )

//object Follower {
//  lazy implicit val followerDecoder: Decoder[Follower] = deriveDecoder
//  lazy implicit val followerEncoder: Encoder[Follower] = deriveEncoder
//}

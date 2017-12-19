package models

case class Like(
  id: Long,
  user: User
)

//object Like {
//  implicit val likeDecoder: Decoder[Like] = deriveDecoder
//  implicit val likeEncoder: Encoder[Like] = deriveEncoder
//}
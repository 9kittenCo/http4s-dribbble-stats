package models

case class Shot(
  id: Long,
  likes_count: Long
)

//object Shot {
//  implicit val shotDecoder: Decoder[Shot] = deriveDecoder
//  implicit val shotEncoder: Encoder[Shot] = deriveEncoder
//
//}
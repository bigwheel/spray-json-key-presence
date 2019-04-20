package spray.json.member

sealed abstract class MemberOption[+A] extends Product with Serializable {
  def toOption: Option[A] = this match {
    case ms: MemberSome[A] => Some(ms.value)
    case MemberNone => None
  }
}

final case class MemberSome[+A](value: A) extends MemberOption[A]

case object MemberNone extends MemberOption[Nothing]

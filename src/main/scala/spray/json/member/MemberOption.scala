package spray.json.member

// this file is copied from Option.scala
object MemberOption {
  import scala.language.implicitConversions
  implicit def memberOption2Iterable[A](xo: MemberOption[A]): Iterable[A] = xo.toList
  def apply[A](x: A): MemberOption[A] = if (x == null) MemberNone else MemberSome(x)
  def empty[A]: MemberOption[A] = MemberNone
}

sealed abstract class MemberOption[+A] extends Product with Serializable {
  self =>

  def isEmpty: Boolean

  def isDefined: Boolean = !isEmpty

  def get: A

  @inline final def getOrElse[B >: A](default: => B): B =
    if (isEmpty) default else this.get

  @inline final def orNull[A1 >: A](implicit ev: Null <:< A1): A1 = this getOrElse ev(null)

  @inline final def map[B](f: A => B): MemberOption[B] =
    if (isEmpty) MemberNone else MemberSome(f(this.get))

  @inline final def fold[B](ifEmpty: => B)(f: A => B): B =
    if (isEmpty) ifEmpty else f(this.get)

  @inline final def flatMap[B](f: A => MemberOption[B]): MemberOption[B] =
    if (isEmpty) MemberNone else f(this.get)

  def flatten[B](implicit ev: A <:< MemberOption[B]): MemberOption[B] =
    if (isEmpty) MemberNone else ev(this.get)

  @inline final def filter(p: A => Boolean): MemberOption[A] =
    if (isEmpty || p(this.get)) this else MemberNone

  @inline final def filterNot(p: A => Boolean): MemberOption[A] =
    if (isEmpty || !p(this.get)) this else MemberNone

  final def nonEmpty = isDefined

  @inline final def withFilter(p: A => Boolean): WithFilter = new WithFilter(p)

  class WithFilter(p: A => Boolean) {
    def map[B](f: A => B): MemberOption[B] = self filter p map f
    def flatMap[B](f: A => MemberOption[B]): MemberOption[B] = self filter p flatMap f
    def foreach[U](f: A => U): Unit = self filter p foreach f
    def withFilter(q: A => Boolean): WithFilter = new WithFilter(x => p(x) && q(x))
  }

  final def contains[A1 >: A](elem: A1): Boolean =
    !isEmpty && this.get == elem

  @inline final def exists(p: A => Boolean): Boolean =
    !isEmpty && p(this.get)

  @inline final def forall(p: A => Boolean): Boolean = isEmpty || p(this.get)

  @inline final def foreach[U](f: A => U) {
    if (!isEmpty) f(this.get)
  }

  @inline final def collect[B](pf: PartialFunction[A, B]): MemberOption[B] =
    if (!isEmpty) pf.lift(this.get).fold[MemberOption[B]](MemberNone)(MemberSome(_)) else MemberNone

  @inline final def orElse[B >: A](alternative: => MemberOption[B]): MemberOption[B] =
    if (isEmpty) alternative else this

  def iterator: Iterator[A] =
    if (isEmpty) collection.Iterator.empty else collection.Iterator.single(this.get)

  def toList: List[A] =
    if (isEmpty) List() else new ::(this.get, Nil)

  @inline final def toRight[X](left: => X): Either[X, A] =
    if (isEmpty) Left(left) else Right(this.get)

  @inline final def toLeft[X](right: => X): Either[A, X] =
    if (isEmpty) Right(right) else Left(this.get)
}

final case class MemberSome[+A](@deprecatedName('x, "2.12.0") value: A) extends MemberOption[A] {
  def isEmpty = false
  def get = value

  @deprecated("Use .value instead.", "2.12.0") def x: A = value
}

case object MemberNone extends MemberOption[Nothing] {
  def isEmpty = true
  def get = throw new NoSuchElementException("KeyNotExist.get")
}


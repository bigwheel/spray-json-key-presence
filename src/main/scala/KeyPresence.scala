// this file is copied from Option.scala
object KeyPresence {
  import scala.language.implicitConversions
  implicit def keyPresence2Iterable[A](xo: KeyPresence[A]): Iterable[A] = xo.toList
  def apply[A](x: A): KeyPresence[A] = if (x == null) KeyNotExist else KeyExist(x)
  def empty[A]: KeyPresence[A] = KeyNotExist
}

sealed abstract class KeyPresence[+A] extends Product with Serializable {
  self =>

  def isEmpty: Boolean

  def isDefined: Boolean = !isEmpty

  def get: A

  @inline final def getOrElse[B >: A](default: => B): B =
    if (isEmpty) default else this.get

  @inline final def orNull[A1 >: A](implicit ev: Null <:< A1): A1 = this getOrElse ev(null)

  @inline final def map[B](f: A => B): KeyPresence[B] =
    if (isEmpty) KeyNotExist else KeyExist(f(this.get))

  @inline final def fold[B](ifEmpty: => B)(f: A => B): B =
    if (isEmpty) ifEmpty else f(this.get)

  @inline final def flatMap[B](f: A => KeyPresence[B]): KeyPresence[B] =
    if (isEmpty) KeyNotExist else f(this.get)

  def flatten[B](implicit ev: A <:< KeyPresence[B]): KeyPresence[B] =
    if (isEmpty) KeyNotExist else ev(this.get)

  @inline final def filter(p: A => Boolean): KeyPresence[A] =
    if (isEmpty || p(this.get)) this else KeyNotExist

  @inline final def filterNot(p: A => Boolean): KeyPresence[A] =
    if (isEmpty || !p(this.get)) this else KeyNotExist

  final def nonEmpty = isDefined

  @inline final def withFilter(p: A => Boolean): WithFilter = new WithFilter(p)

  class WithFilter(p: A => Boolean) {
    def map[B](f: A => B): KeyPresence[B] = self filter p map f
    def flatMap[B](f: A => KeyPresence[B]): KeyPresence[B] = self filter p flatMap f
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

  @inline final def collect[B](pf: PartialFunction[A, B]): KeyPresence[B] =
    if (!isEmpty) pf.lift(this.get).fold[KeyPresence[B]](KeyNotExist)(KeyExist(_)) else KeyNotExist

  @inline final def orElse[B >: A](alternative: => KeyPresence[B]): KeyPresence[B] =
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

final case class KeyExist[+A](@deprecatedName('x, "2.12.0") value: A) extends KeyPresence[A] {
  def isEmpty = false
  def get = value

  @deprecated("Use .value instead.", "2.12.0") def x: A = value
}

case object KeyNotExist extends KeyPresence[Nothing] {
  def isEmpty = true
  def get = throw new NoSuchElementException("KeyNotExist.get")
}

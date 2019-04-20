package spray.json.member

import spray.json._

trait MemberPresenceFormats {
  _: StandardFormats with ProductFormats =>

  implicit def memberOptionFormat[T: JF]: JF[MemberOption[T]] = new MemberOptionFormat[T]

  class MemberOptionFormat[T: JF] extends JF[MemberOption[T]] {
    def write(option: MemberOption[T]) = option match {
      case MemberSome(x) => x.toJson
      case MemberNone => JsNull
    }
    def read(value: JsValue) = MemberSome(value.convertTo[T])
  }

  override protected def fromField[T](value: JsValue, fieldName: String)(
      implicit reader: JsonReader[T]
  ) = value match {
    case x: JsObject
        if reader.isInstanceOf[MemberOptionFormat[_]] &
          !x.fields.contains(fieldName) =>
      MemberNone.asInstanceOf[T]
    case x: JsObject =>
      try reader.read(x.fields(fieldName))
      catch {
        case e: NoSuchElementException =>
          deserializationError(
            "Object is missing required member '" + fieldName + "'",
            e,
            fieldName :: Nil
          )
        case DeserializationException(msg, cause, fieldNames) =>
          deserializationError(msg, cause, fieldName :: fieldNames)
      }
    case _ =>
      deserializationError(
        "Object expected in field '" + fieldName + "'",
        fieldNames = fieldName :: Nil
      )
  }
}

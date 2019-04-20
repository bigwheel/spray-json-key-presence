package spray.json.member

import spray.json._

trait KeyPresenceFormat {
  _: StandardFormats with ProductFormats =>

  implicit def keyPresenceFormat[T: JF]: JF[KeyPresence[T]] = new KeyPresenceFormat[T]

  class KeyPresenceFormat[T: JF] extends JF[KeyPresence[T]] {
    def write(option: KeyPresence[T]) = option match {
      case KeyExist(x) => x.toJson
      case KeyNotExist => JsNull
    }
    def read(value: JsValue) = KeyExist(value.convertTo[T])
  }

  override protected def fromField[T](value: JsValue, fieldName: String)(
      implicit reader: JsonReader[T]
  ) = value match {
    case x: JsObject
        if reader.isInstanceOf[KeyPresenceFormat[_]] &
          !x.fields.contains(fieldName) =>
      KeyNotExist.asInstanceOf[T]
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

trait KeyPresenceJsonProtocol extends DefaultJsonProtocol with KeyPresenceFormat

object KeyPresenceJsonProtocol extends KeyPresenceJsonProtocol

import spray.json._
import tekito._

object Main {

  def main(args: Array[String]): Unit = {
    val nullValue = """{ "a": null }""".parseJson
    val noKeyPresence = """{}""".parseJson

    case class CaseClassA(a: Option[Int])
    object CaseClassA extends DefaultJsonProtocol {
      implicit val caseClassAFormat = jsonFormat1(CaseClassA.apply)
    }

    println(nullValue.convertTo[CaseClassA]) // CaseClassA(None)
    println(noKeyPresence.convertTo[CaseClassA]) // CaseClassA(None)
    println("Cannot distinguish 😞")

    case class CaseClassB(a: KeyPresence[Option[Int]])
    object CaseClassB extends KeyPresenceJsonProtocol {
      implicit val caseClassBFormat = jsonFormat1(CaseClassB.apply)
    }

    println(nullValue.convertTo[CaseClassB]) // CaseClassB(KeyExist(None))
    println(noKeyPresence.convertTo[CaseClassB]) // CaseClassB(KeyNotExist)
    println("Now we can, yeah ☺️")
  }

}

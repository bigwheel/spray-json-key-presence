import spray.json._
import spray.json.member._

object Main {

  def main(args: Array[String]): Unit = {
    val nullValue = """{ "a": null }""".parseJson
    val noMember = """{}""".parseJson

    case class CaseClassA(a: Option[Int])
    object CaseClassA extends DefaultJsonProtocol {
      implicit val caseClassAFormat = jsonFormat1(CaseClassA.apply)
    }

    println(nullValue.convertTo[CaseClassA]) // CaseClassA(None)
    println(noMember.convertTo[CaseClassA]) // CaseClassA(None)
    println("Cannot distinguish 😞")

    case class CaseClassB(a: MemberOption[Option[Int]])
    object CaseClassB extends MemberPresenceJsonProtocol {
      implicit val caseClassBFormat = jsonFormat1(CaseClassB.apply)
    }

    println(nullValue.convertTo[CaseClassB]) // CaseClassB(KeyExist(None))
    println(noMember.convertTo[CaseClassB]) // CaseClassB(KeyNotExist)
    println("Now we can, yeah ☺️")
  }

}

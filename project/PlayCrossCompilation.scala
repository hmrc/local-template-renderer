import uk.gov.hmrc.playcrosscompilation.{AbstractPlayCrossCompilation, PlayVersion}
import uk.gov.hmrc.playcrosscompilation.PlayVersion._

object PlayCrossCompilation extends AbstractPlayCrossCompilation(defaultPlayVersion = Play26) {

  def version: String = playVersion match {
    case Play26 => "2.6.11"
    case Play27 => "2.7.5"
    case Play28 => "2.8.7"
  }

  override def playCrossScalaBuilds(scalaVersions: Seq[String]): Seq[String] =
    playVersion match {
      case Play26 => scalaVersions
      case Play27 => scalaVersions.filter(version => version.startsWith("2.12"))
      case Play28 => scalaVersions.filter(version => version.startsWith("2.12"))
    }
}

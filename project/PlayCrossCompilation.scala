import uk.gov.hmrc.playcrosscompilation.{AbstractPlayCrossCompilation, PlayVersion}
import uk.gov.hmrc.playcrosscompilation.PlayVersion._

object PlayCrossCompilation extends AbstractPlayCrossCompilation(defaultPlayVersion = Play26) {

  def version: String = playVersion match {
    case Play26 => "2.6.11"
    case Play25 => "2.5.19"
    case Play27 => "2.7.5"
  }

  override def playCrossScalaBuilds(scalaVersions: Seq[String]): Seq[String] =
    playVersion match {
      case PlayVersion.Play25 => scalaVersions.filter(version => version.startsWith("2.11"))
      case PlayVersion.Play26 => scalaVersions
      case PlayVersion.Play27 => scalaVersions.filter(version => version.startsWith("2.12"))
    }
}
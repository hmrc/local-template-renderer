import uk.gov.hmrc.playcrosscompilation.AbstractPlayCrossCompilation
import uk.gov.hmrc.playcrosscompilation.PlayVersion._

object PlayCrossCompilation extends AbstractPlayCrossCompilation(defaultPlayVersion = Play26) {

  def version: String = playVersion match {
    case Play26 => "2.6.11"
    case Play25 => "2.5.19"
  }
}
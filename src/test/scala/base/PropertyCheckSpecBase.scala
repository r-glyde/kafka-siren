package base

import org.scalatest.{Matchers, WordSpecLike}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

trait PropertyCheckSpecBase extends WordSpecLike with Matchers with ScalaCheckPropertyChecks

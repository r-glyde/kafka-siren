package base

import net.manub.embeddedkafka.EmbeddedKafka
import org.scalatest.{EitherValues, Matchers, fixture}

abstract class FeatureSpecBase
  extends fixture.FeatureSpec
    with fixture.ConfigMapFixture
    with Matchers
    with EmbeddedKafka
    with EitherValues

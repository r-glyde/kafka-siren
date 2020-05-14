package base

import net.manub.embeddedkafka.EmbeddedKafka
import org.scalatest.{BeforeAndAfterAll, EitherValues, Matchers, fixture}
import sttp.client.HttpURLConnectionBackend

abstract class FeatureSpecBase
  extends fixture.FeatureSpec
    with fixture.ConfigMapFixture
    with BeforeAndAfterAll
    with Matchers
    with EmbeddedKafka
    with EitherValues {

  implicit val backend = HttpURLConnectionBackend()

  override def afterAll(): Unit = backend.close()

}

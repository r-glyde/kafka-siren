package base

import cats.effect.{ContextShift, IO, Timer}
import scala.concurrent.ExecutionContext

trait IOSpecBase {

  val executionContext: ExecutionContext        = ExecutionContext.global
  implicit val ioContextShift: ContextShift[IO] = IO.contextShift(executionContext)
  implicit val ioTimer: Timer[IO]               = IO.timer(executionContext)

}

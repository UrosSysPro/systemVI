package net.systemvi.website.services

import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom
import net.systemvi.website.utils.Constants


object UserService {
  import scala.concurrent.ExecutionContext

  given ExecutionContext = ExecutionContext.global

  def whoAmI = EventStream.fromFuture(
      dom.fetch(s"${Constants.serverUrl}/user")
        .toFuture
        .flatMap(_.json().toFuture)
    )
}

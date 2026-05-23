package net.systemvi.server.api.middleware

import org.http4s.*
import org.http4s.implicits.*
import org.http4s.dsl.io.*
import org.http4s.headers.Origin.Host
import org.http4s.server.middleware.CORS

val cors = CORS.policy
  .withAllowOriginHost(url => url.host.value == "localhost")
  .withAllowCredentials(true)
  .withAllowMethodsIn(Set(
    Method.OPTIONS,
    Method.GET,
    Method.POST,
    Method.DELETE,
    Method.PUT,
  ))


package net.systemvi.server.api.middleware

import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.headers.Origin.Host
import org.http4s.server.middleware.CORS

val cors = CORS.policy.withAllowOriginHost{ case Host(scheme,host,port) =>
  (scheme,host,port) match {
    case _ if host.value=="localhost" && scheme == Uri.Scheme.http => true
    case _ if host.value=="systemvi.net" && scheme == Uri.Scheme.https => true
    case _ => false
  }
}

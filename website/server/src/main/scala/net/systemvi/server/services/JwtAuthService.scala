package net.systemvi.server.services

import cats.*
import cats.implicits.*
import cats.effect.*
import cats.effect.implicits.*
import org.http4s.*
import org.http4s.implicits.*
import org.http4s.UriTemplate.*

trait JwtAuthService[F[_]] {

}

object JwtAuthService {
  def create[F[_]: Async]: JwtAuthService[F] = new JwtAuthService[F] {
    
  }
}

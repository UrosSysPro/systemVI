package net.systemvi.server.api.controllers

import cats.*
import cats.effect.*
import cats.implicits.*
import io.circe.*
import io.circe.generic.auto.*
import io.circe.syntax.*
import net.systemvi.server.persistance.contexts.*
import net.systemvi.server.persistance.models.*
import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.headers.*

import pdi.jwt.*
import java.time.Instant

import java.util.UUID
import scala.util.*

def googleAuthController(using context: AppContext[IO]) = {
  val googleApiService = context.googleApiService
  val googleUriService = context.googleUriService

  HttpRoutes.of[IO] {
    case request @ GET -> Root / "redirect" => for {
      url <- googleUriService.getLoginRedirectUri(context)
      response <- Found(Location(url))
    } yield response

    case request @ GET -> Root / "callback" :? query => {
      val code = query("code").toList.head

      val result = for {
        // get token
        tokenResponse <- googleApiService.getAccessToken(context, code)

        userProfile <- googleApiService.getUserProfile(context, tokenResponse.access_token)
        googleAccountInDb <- context.db.googleAccounts.get(userProfile.sub)
        googleAcc <- googleAccountInDb match {
          case Some(acc) => IO{acc}
          case None =>{
            val user = User(
              UUID.randomUUID(),
              name = userProfile.name.getOrElse(""),
              email = userProfile.email,
              picture = userProfile.picture,
            )
            for{
              _<-context.db.users.add(user)
              account<-context.db.googleAccounts.add(userProfile,user)
            }yield (account)
          }
        }

        user <- context.db.users.get(googleAcc.userUUID)

        claim = JwtClaim(
          content = user.asJson.noSpaces,
          expiration = Instant.now.plusSeconds(7 * 24 * 60 * 60).getEpochSecond.some,
          issuedAt = Instant.now.getEpochSecond.some,
        )

        key = context.config.jwtAuthConfig.secret
        algo = JwtAlgorithm.HS256
        token = JwtCirce.encode(claim,key,algo)
        cookie = ResponseCookie(
          name = "access_token",
          content = token,
          expires = HttpDate.unsafeFromInstant(Instant.now.plusSeconds(7 * 24 * 60 * 60)).some,
          httpOnly = true,
          secure = true,
          path = "/".some
        )
        uri <- context.jwtAuthUriService.getUserProfilePageUrl(context)
        response <- Found(
          Location(uri)
        ).map(_.addCookie(cookie))
      } yield response

      result.attempt.flatMap {
        case Left(throwable) => Ok(s"fail ${throwable.getMessage}")
        case Right(result) => IO{result}
      }
    }
  }
}

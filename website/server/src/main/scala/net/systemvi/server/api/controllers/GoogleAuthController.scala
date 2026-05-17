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
import org.http4s.headers.Location

import java.util.UUID
import scala.util.*

def googleAuthController(using context: AppContext[IO]) = {
  val googleApiService = context.googleApiService
  val googleUriService = context.googleUriService

  HttpRoutes.of[IO] {
    case request@GET -> Root / "redirect" => for {
      url <- googleUriService.getLoginRedirectUri(context)
      response <- Found(Location(url))
    } yield response

    case request@GET -> Root / "callback" :? query => {
      val code = query("code").toList.head

      val result = for {
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
        response <- Ok(googleAcc.asJson.noSpaces)
      } yield response

      result.attempt.flatMap {
        case Left(throwable) => Ok(s"fail ${throwable.getMessage}")
        case Right(result) => IO{result}
      }
    }
  }
}

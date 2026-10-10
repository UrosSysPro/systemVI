package net.systemvi.mainmenu.server.services

import net.systemvi.mainmenu.server.config.JwtAuthConfig
import pdi.jwt.*

import java.time.Instant
import scala.concurrent.duration.*

/**
 * Mints HS256 session tokens.
 *
 * Verification is done by the JWT middleware (which shares the same secret), so
 * this service only needs to sign a claim. The token lifetime is also used for
 * the session cookie, keeping the two in sync.
 */
final class JwtService(config: JwtAuthConfig) {
  val ttl: FiniteDuration = 7.days

  /** Signs `content` (typically the session user serialised to JSON). */
  def encode(content: String): String = {
    val now = Instant.now()
    val claim = JwtClaim(
      content = content,
      issuedAt = Some(now.getEpochSecond),
      expiration = Some(now.plusSeconds(ttl.toSeconds).getEpochSecond),
    )
    JwtCirce.encode(claim, config.secret, JwtAlgorithm.HS256)
  }
}

object JwtService {
  def apply(config: JwtAuthConfig): JwtService = new JwtService(config)
}

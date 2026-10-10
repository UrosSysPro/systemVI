package net.systemvi.mainmenu.server.services

import munit.FunSuite
import net.systemvi.mainmenu.server.config.JwtAuthConfig
import pdi.jwt.*

import java.time.Instant

class JwtServiceTest extends FunSuite {

  private val jwt = JwtService(JwtAuthConfig("test-jwt-secret"))

  test("a minted token verifies with the same secret and carries the content") {
    val content = """{"uuid":"abc"}"""
    val token = jwt.encode(content)
    val decoded = JwtCirce.decode(token, "test-jwt-secret", Seq(JwtAlgorithm.HS256))
    assert(decoded.isSuccess, decoded.toString)
    assertEquals(decoded.get.content, content)
  }

  test("a token signed with a different secret does not verify") {
    val token = jwt.encode("{}")
    assert(JwtCirce.decode(token, "other-secret", Seq(JwtAlgorithm.HS256)).isFailure)
  }

  test("the token is issued now and expires after the ttl") {
    val token = jwt.encode("{}")
    val claim = JwtCirce.decode(token, "test-jwt-secret", Seq(JwtAlgorithm.HS256)).get
    val now = Instant.now().getEpochSecond

    assert(claim.issuedAt.exists(now - 5 <= _), claim.issuedAt)
    assert(
      claim.expiration.exists(exp => math.abs(exp - (now + jwt.ttl.toSeconds)) <= 5),
      claim.expiration,
    )
  }
}

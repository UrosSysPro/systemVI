package net.systemvi.mainmenu.server.persistance.migrations

import cats.effect.*
import cats.syntax.all.*
import doobie.*
import doobie.implicits.*
import munit.CatsEffectSuite
import net.systemvi.mainmenu.server.TestDatabase

class MigrationRunnerTest extends CatsEffectSuite {

  private def recordedCount(xa: Transactor[IO]): IO[Int] =
    sql"select count(*) from SchemaMigrations".query[Int].unique.transact(xa)

  private def itemColumns(xa: Transactor[IO]): IO[List[String]] =
    sql"select name from pragma_table_info('Items')".query[String].to[List].transact(xa)

  test("up applies every pending migration in order and records them once") {
    TestDatabase.empty.use { xa =>
      val expected = Migrations.all(xa).map(_.name)
      for {
        first <- MigrationRunner.up(xa)
        second <- MigrationRunner.up(xa)
        recorded <- recordedCount(xa)
      } yield {
        assertEquals(first, expected)
        assertEquals(second, Nil)
        assertEquals(recorded, expected.size)
      }
    }
  }

  test("down reverts the most recently applied migration and can be repeated") {
    TestDatabase.empty.use { xa =>
      val expected = Migrations.all(xa).map(_.name)
      for {
        _ <- MigrationRunner.up(xa)
        reverted <- expected.reverse.traverse(_ => MigrationRunner.down(xa))
        extra <- MigrationRunner.down(xa)
        recorded <- recordedCount(xa)
      } yield {
        assertEquals(reverted, expected.reverse.map(Some(_)))
        assertEquals(extra, None)
        assertEquals(recorded, 0)
      }
    }
  }

  test("the alter-items migration adds the categoryId column and its down removes it") {
    TestDatabase.empty.use { xa =>
      val items = __02_create_items_table[IO](xa)
      val alter = __04_alter_items_add_categoryId_column[IO](xa)
      for {
        _ <- items.up
        before <- itemColumns(xa)
        _ <- alter.up
        during <- itemColumns(xa)
        _ <- alter.down
        after <- itemColumns(xa)
      } yield {
        assert(!before.contains("categoryId"), s"expected no categoryId in $before")
        assert(during.contains("categoryId"), s"expected categoryId in $during")
        assert(!after.contains("categoryId"), s"expected categoryId gone from $after")
      }
    }
  }

  test("down on an unmigrated database does nothing") {
    TestDatabase.empty.use { xa =>
      MigrationRunner.down(xa).map(result => assertEquals(result, None))
    }
  }
}

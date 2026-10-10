package net.systemvi.mainmenu.server.persistance.migrations

import cats.effect.*
import doobie.*
import doobie.implicits.*
import munit.CatsEffectSuite
import net.systemvi.mainmenu.server.TestDatabase

class MigrationRunnerTest extends CatsEffectSuite {

  private val allMigrationNames = List(
    "__01_create_menus_table",
    "__02_create_items_table",
    "__03_create_categories_table",
    "__04_alter_items_add_categoryId_column",
  )

  private def recordedCount(xa: Transactor[IO]): IO[Int] =
    sql"select count(*) from SchemaMigrations".query[Int].unique.transact(xa)

  private def itemColumns(xa: Transactor[IO]): IO[List[String]] =
    sql"select name from pragma_table_info('Items')".query[String].to[List].transact(xa)

  test("up applies every pending migration in order and records them once") {
    TestDatabase.empty.use { xa =>
      for {
        first <- MigrationRunner.up(xa)
        second <- MigrationRunner.up(xa)
        recorded <- recordedCount(xa)
      } yield {
        assertEquals(first, allMigrationNames)
        assertEquals(second, Nil)
        assertEquals(recorded, allMigrationNames.size)
      }
    }
  }

  test("down reverts the most recently applied migration and can be repeated") {
    TestDatabase.empty.use { xa =>
      for {
        _ <- MigrationRunner.up(xa)
        first <- MigrationRunner.down(xa)
        second <- MigrationRunner.down(xa)
        third <- MigrationRunner.down(xa)
        fourth <- MigrationRunner.down(xa)
        fifth <- MigrationRunner.down(xa)
        recorded <- recordedCount(xa)
      } yield {
        assertEquals(first, Some("__04_alter_items_add_categoryId_column"))
        assertEquals(second, Some("__03_create_categories_table"))
        assertEquals(third, Some("__02_create_items_table"))
        assertEquals(fourth, Some("__01_create_menus_table"))
        assertEquals(fifth, None)
        assertEquals(recorded, 0)
      }
    }
  }

  test("the alter migration adds the categoryId column and down removes it") {
    TestDatabase.empty.use { xa =>
      for {
        _ <- MigrationRunner.up(xa)
        afterUp <- itemColumns(xa)
        _ <- MigrationRunner.down(xa)
        afterDown <- itemColumns(xa)
      } yield {
        assert(afterUp.contains("categoryId"), s"expected categoryId in $afterUp")
        assert(!afterDown.contains("categoryId"), s"expected categoryId gone from $afterDown")
      }
    }
  }

  test("down on an unmigrated database does nothing") {
    TestDatabase.empty.use { xa =>
      MigrationRunner.down(xa).map(result => assertEquals(result, None))
    }
  }
}

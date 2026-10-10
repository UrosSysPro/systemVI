package net.systemvi.mainmenu.server.services

import net.systemvi.mainmenu.common.dto.CategoryDto
import net.systemvi.mainmenu.server.persistance.models.Category

import java.util.UUID

/** Assembles the flat list of categories into a nested [[CategoryDto]] tree. */
object CategoryTree {

  /** The full forest of top-level categories, each carrying its descendants. */
  def forest(categories: List[Category]): List[CategoryDto] = {
    val children = childrenByParent(categories)
    children.getOrElse(None, Nil).map(toDto(children)).sortBy(_.name)
  }

  /** The subtree rooted at `uuid`, or `None` if no such category exists. */
  def subtree(categories: List[Category], uuid: UUID): Option[CategoryDto] = {
    val children = childrenByParent(categories)
    categories.find(_.uuid == uuid).map(toDto(children))
  }

  private def childrenByParent(categories: List[Category]): Map[Option[UUID], List[Category]] =
    categories.groupBy(_.parentId)

  private def toDto(children: Map[Option[UUID], List[Category]])(category: Category): CategoryDto =
    CategoryDto(
      category.uuid,
      category.name,
      category.description,
      category.imageUrl,
      category.parentId,
      category.restaurantId,
      children.getOrElse(Some(category.uuid), Nil).map(toDto(children)).sortBy(_.name),
    )
}

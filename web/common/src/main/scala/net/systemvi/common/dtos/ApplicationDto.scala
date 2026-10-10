package net.systemvi.common.dtos

import java.util.UUID

case class ApplicationCategoryDto(
                                   id: Int,
                                   name: String,
                                 )

case class ApplicationDto(
                           uuid: UUID,
                           category: ApplicationCategoryDto,
                           images: List[EntityImageDto],
                           name: String,
                           codeName: String,
                           description: String,
                         )


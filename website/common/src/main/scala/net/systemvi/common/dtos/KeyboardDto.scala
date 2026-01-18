package net.systemvi.common.dtos

import java.util.UUID

case class KeyboardProfileDto(
                                id:Int,
                                name:String,
                             )

case class KeyboardDto(
                        uuid: UUID,
                        switch: SwitchDto,
                        profile: KeyboardProfileDto,
                        name: String,
                        codeName: String,
                        images: List[EntityImageDto],
                        specs: List[EntitySpecificationDto],
                      )

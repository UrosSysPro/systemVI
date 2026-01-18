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
                        filament: FilamentDto,
                        images: List[EntityImageDto],
                        specs: List[EntitySpecificationDto],
                        name: String,
                        codeName: String,
                      )

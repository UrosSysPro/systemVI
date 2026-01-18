package net.systemvi.common.dtos

import java.util.UUID

case class SwitchTypeDto(
                          id: Int,
                          name: String,
                        )

case class SwitchDto(
                      uuid: UUID,
                      manufacturer: ManufacturerDto,
                      switchType: SwitchTypeDto,
                      name: String,
                    ) 

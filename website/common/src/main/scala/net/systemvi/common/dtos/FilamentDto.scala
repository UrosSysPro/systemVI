package net.systemvi.common.dtos

case class FilamentPolymerDto(
                              name: String
                             )
case class FilamentDto(
                        polymer: FilamentPolymerDto,
                        manufacturer: ManufacturerDto,
                        name: String,
                      )


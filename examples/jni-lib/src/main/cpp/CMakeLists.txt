add_library(tools SHARED "")
target_sources(tools PRIVATE tools.cpp)

add_library(sdf SHARED "")
target_sources(sdf PRIVATE SDF.cpp)
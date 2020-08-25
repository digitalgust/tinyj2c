cmake_minimum_required(VERSION 3.6)
project(ccjvm C)


enable_language(C)

set(CMAKE_C_STANDARD 99)

include_directories(app/vm app/out/c app/gui)

LINK_DIRECTORIES(app/vm/)

aux_source_directory(./app/vm VM_FILES)
aux_source_directory(./app/gui GUI_FILES)
aux_source_directory(./app/out/c SOURCE_FILES)



add_executable(ccjvm ${VM_FILES} ${SOURCE_FILES} ${GUI_FILES})
#find_library(MY_LIB libcc.a "${CMAKE_SOURCE_DIR}/app/vm")
#TARGET_LINK_LIBRARIES(ccjvm ${MY_LIB})
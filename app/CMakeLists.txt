cmake_minimum_required(VERSION 3.6)
project(ccjvm C)


enable_language(C)

set(CMAKE_C_STANDARD 99)

include_directories(vm out/c gui vm/https vm/https/mbedtls/include)

LINK_DIRECTORIES(vm/)

aux_source_directory(vm VM_FILES)
#aux_source_directory(vm/https VMHTTPS_FILES)
#aux_source_directory(vm/https/mbedtls/library VMMBEDTLS_FILES)
#aux_source_directory(gui GUI_FILES)
aux_source_directory(out/c SOURCE_FILES)
aux_source_directory(option_pack/file FILEPACK_FILES)


add_executable(ccjvm ${VM_FILES} ${VMHTTPS_FILES} ${VMMBEDTLS_FILES} ${SOURCE_FILES} ${GUI_FILES} ${FILEPACK_FILES})
#find_library(MY_LIB libcc.a "${CMAKE_SOURCE_DIR}/app/vm")
#TARGET_LINK_LIBRARIES(ccjvm ${MY_LIB})


IF (APPLE)
ELSEIF (UNIX)
    TARGET_LINK_LIBRARIES(ccjvm dl)
ELSEIF (MINGW)
    TARGET_LINK_LIBRARIES(ccjvm ws2_32)
    IF (CMAKE_SIZEOF_VOID_P EQUAL 4)
    elseif (CMAKE_SIZEOF_VOID_P EQUAL 8)
    endif ()
ENDIF ()
cmake_minimum_required(VERSION 3.6)
project(ccjvm C)


enable_language(C)

set(CMAKE_C_STANDARD 99)

include_directories(app/vm app/out/c app/gui app/vm/https app/vm/https/mbedtls/include)

LINK_DIRECTORIES(app/vm/)

aux_source_directory(./app/vm VM_FILES)
aux_source_directory(./app/vm/https VMHTTPS_FILES)
aux_source_directory(./app/vm/https/mbedtls/library VMMBEDTLS_FILES)
aux_source_directory(./app/gui GUI_FILES)
aux_source_directory(./app/out/c SOURCE_FILES)


add_executable(ccjvm ${VM_FILES} ${VMHTTPS_FILES} ${VMMBEDTLS_FILES} ${SOURCE_FILES} ${GUI_FILES})
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
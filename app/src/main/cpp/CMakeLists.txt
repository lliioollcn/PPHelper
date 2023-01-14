cmake_minimum_required(VERSION 3.18.1)
project("pphelper")
add_subdirectory(../../../../dexkit dexkit)
add_subdirectory(../../../../mmkv mmkv)


add_library(pphelper SHARED native.cpp)
target_include_directories(pphelper PUBLIC include)
add_library(avcodec STATIC IMPORTED)
add_library(avfilter STATIC IMPORTED)
add_library(avformat STATIC IMPORTED)
add_library(swresample STATIC IMPORTED)
add_library(swscale STATIC IMPORTED)
add_library(avutil STATIC IMPORTED)
add_library(mp3lame STATIC IMPORTED)
add_library(postproc STATIC IMPORTED)
set_target_properties(avcodec PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/static/libavcodec.a)
set_target_properties(avfilter PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/static/libavfilter.a)
set_target_properties(avutil PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/static/libavutil.a)
set_target_properties(avformat PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/static/libavformat.a)
set_target_properties(swresample PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/static/libswresample.a)
set_target_properties(swscale PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/static/libswscale.a)
set_target_properties(mp3lame PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/static/libmp3lame.a)
set_target_properties(postproc PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/static/libpostproc.a)



target_link_libraries(pphelper dexkit mmkv avutil avcodec avformat avfilter swresample swscale mp3lame postproc z)

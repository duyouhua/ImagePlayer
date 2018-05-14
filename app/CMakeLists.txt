cmake_minimum_required(VERSION 3.4.1)

# OpenCV
set(OpenCV4Android ${PROJECT_SOURCE_DIR}/src/main/cpp/OpenCV)
include_directories(${OpenCV4Android}/include)

add_library(opencv_java3 SHARED IMPORTED) 
set_target_properties(opencv_java3 PROPERTIES IMPORT_LOCATION ${OpenCV4Android}/libs/${ANDROID_ABI}/libopencv_java3.so)

add_library(opencv_calib3d STATIC IMPORTED) 
set_target_properties(opencv_calib3d PROPERTIES IMPORT_LOCATION ${OpenCV4Android}/libs/${ANDROID_ABI}/libopencv_calib3d.a)

add_library(opencv_core STATIC IMPORTED) 
set_target_properties(opencv_core PROPERTIES IMPORT_LOCATION ${OpenCV4Android}/libs/${ANDROID_ABI}/libopencv_core.a)

add_library(opencv_dnn STATIC IMPORTED) 
set_target_properties(opencv_dnn PROPERTIES IMPORT_LOCATION ${OpenCV4Android}/libs/${ANDROID_ABI}/libopencv_dnn.a)

add_library(opencv_features2d STATIC IMPORTED) 
set_target_properties(opencv_features2d PROPERTIES IMPORT_LOCATION ${OpenCV4Android}/libs/${ANDROID_ABI}/libopencv_features2d.a)

add_library(opencv_flann STATIC IMPORTED) 
set_target_properties(opencv_flann PROPERTIES IMPORT_LOCATION ${OpenCV4Android}/libs/${ANDROID_ABI}/libopencv_flann.a)

add_library(opencv_highgui STATIC IMPORTED) 
set_target_properties(opencv_highgui PROPERTIES IMPORT_LOCATION ${OpenCV4Android}/libs/${ANDROID_ABI}/libopencv_highgui.a)

add_library(opencv_imgcodecs STATIC IMPORTED) 
set_target_properties(opencv_imgcodecs PROPERTIES IMPORT_LOCATION ${OpenCV4Android}/libs/${ANDROID_ABI}/libopencv_imgcodecs.a)

add_library(opencv_imgproc STATIC IMPORTED) 
set_target_properties(opencv_imgproc PROPERTIES IMPORT_LOCATION ${OpenCV4Android}/libs/${ANDROID_ABI}/libopencv_imgproc.a)

add_library(opencv_ml STATIC IMPORTED) 
set_target_properties(opencv_ml PROPERTIES IMPORT_LOCATION ${OpenCV4Android}/libs/${ANDROID_ABI}/libopencv_ml.a)

add_library(opencv_objdetect STATIC IMPORTED) 
set_target_properties(opencv_objdetect PROPERTIES IMPORT_LOCATION ${OpenCV4Android}/libs/${ANDROID_ABI}/libopencv_objdetect.a)

add_library(opencv_photo STATIC IMPORTED) 
set_target_properties(opencv_photo PROPERTIES IMPORT_LOCATION ${OpenCV4Android}/libs/${ANDROID_ABI}/libopencv_photo.a)

add_library(opencv_shape STATIC IMPORTED) 
set_target_properties(opencv_shape PROPERTIES IMPORT_LOCATION ${OpenCV4Android}/libs/${ANDROID_ABI}/libopencv_shape.a)

add_library(opencv_stitching STATIC IMPORTED) 
set_target_properties(opencv_stitching PROPERTIES IMPORT_LOCATION ${OpenCV4Android}/libs/${ANDROID_ABI}/libopencv_stitching.a)

add_library(opencv_superres STATIC IMPORTED) 
set_target_properties(opencv_superres PROPERTIES IMPORT_LOCATION ${OpenCV4Android}/libs/${ANDROID_ABI}/libopencv_superres.a)

add_library(opencv_video STATIC IMPORTED) 
set_target_properties(opencv_video PROPERTIES IMPORT_LOCATION ${OpenCV4Android}/libs/${ANDROID_ABI}/libopencv_video.a)

add_library(opencv_videoio STATIC IMPORTED) 
set_target_properties(opencv_videoio PROPERTIES IMPORT_LOCATION ${OpenCV4Android}/libs/${ANDROID_ABI}/libopencv_videoio.a)

add_library(opencv_videostab STATIC IMPORTED) 
set_target_properties(opencv_videostab PROPERTIES IMPORT_LOCATION ${OpenCV4Android}/libs/${ANDROID_ABI}/libopencv_videostab.a)

# Source Code
set(ROOT ${PROJECT_SOURCE_DIR}/src/main/cpp)
add_library( ImagePlayer
             SHARED
             ${ROOT}/JniWarpper.cpp
             # ${ROOT}/ImagePlayer.cpp
             ${ROOT}/ImageBuffer.cpp
             ${ROOT}/StringConvert.cpp)

find_library(log-lib log)

target_link_libraries( ImagePlayer
                       ${opencv_java3}
					   ${opencv_calib3d}
					   ${opencv_core}
					   ${opencv_dnn}
					   ${opencv_features2d}
					   ${opencv_flann}
					   ${opencv_highgui}
					   ${opencv_imgcodecs}
					   ${opencv_imgproc}
					   ${opencv_ml}
					   ${opencv_objdetect}
					   ${opencv_photo}
					   ${opencv_shape}
					   ${opencv_stitching}
					   ${opencv_superres}
					   ${opencv_video}
					   ${opencv_videoio}
					   ${opencv_videostab}
                       ${log-lib} )

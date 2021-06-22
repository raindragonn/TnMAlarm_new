LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := mk
LOCAL_SRC_FILES := mk.c

include $(BUILD_SHARED_LIBRARY)
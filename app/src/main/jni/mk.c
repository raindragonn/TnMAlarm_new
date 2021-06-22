#include <jni.h>




JNIEXPORT jstring JNICALL
Java_com_bluepig_tnmalarm_utils_MyEncoder_bluePig(JNIEnv *env, jobject thiz) {
    return (*env)->NewStringUTF(env, "cnp2T09qVElScnhXTDUxRUZKQ1g1UT09");
}


JNIEXPORT jstring JNICALL
Java_com_bluepig_tnmalarm_utils_MyEncoder_tnmAlarm(JNIEnv *env, jobject thiz) {
    return (*env)->NewStringUTF(env, "c2t3anNkeWQxIXNrd2pzZHlkMSFza3dqc2R5ZDEhMSE=");
}

JNIEXPORT jstring JNICALL
Java_com_bluepig_tnmalarm_lib_TnMAdsImpl_interstitialAd(JNIEnv *env, jobject thiz) {
    return (*env)->NewStringUTF(env, "Y2EtYXBwLXB1Yi05NDc2MTM4MDI4ODA5NDQ0Lzk0ODkzMjc2NTE=");
}

JNIEXPORT jstring JNICALL
Java_com_bluepig_tnmalarm_lib_TnMAdsImpl_bannerAd(JNIEnv *env, jobject thiz) {
    return (*env)->NewStringUTF(env, "Y2EtYXBwLXB1Yi05NDc2MTM4MDI4ODA5NDQ0Lzg0NzkzNjMyMzg=");
}

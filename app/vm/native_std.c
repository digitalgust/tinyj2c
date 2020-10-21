//
// Created by Gust on 2020/5/20.
//

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <sys/time.h>
#include <time.h>
#include <unistd.h>
#include <dirent.h>
#include <errno.h>
#include <stdlib.h>
#include <math.h>
#include <sys/stat.h>


#include "jvm.h"
#include "../out/c/metadata.h"

#include "jni.h"
#include "bytebuf.h"
#include "miniz_wrapper.h"
#include "garbage.h"


#if __JVM_OS_VS__ || __JVM_OS_MINGW__ || __JVM_OS_CYGWIN__

#include <WinSock2.h>
#include <Ws2tcpip.h>

typedef int socklen_t;
#pragma comment(lib, "Ws2_32.lib")
#define SHUT_RDWR SD_BOTH
#define SHUT_RD SD_RECEIVE
#define SHUT_WR SD_SEND
#else

#include <sys/types.h>
#include <sys/socket.h>
#include <sys/select.h>
#include <unistd.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <fcntl.h>

#define INVALID_SOCKET    -1
#define SOCKET_ERROR      -1
#define closesocket(fd)   close(fd)
#endif
#if  defined(__JVM_OS_MAC__) || defined(__JVM_OS_LINUX__)

#include <dlfcn.h>
//#include <glfm.h>

#else

#include <rpc.h>

#endif

#if __JVM_OS_VS__
#include "dirent_win.h"
#else

#include <dirent.h>
#include <unistd.h>

#endif

#include "./https/ssl_client.h"

//=================================  assist ====================================



//=================================  socket  ====================================

#define  SOCK_OP_TYPE_NON_BLOCK   0
#define  SOCK_OP_TYPE_REUSEADDR   1
#define  SOCK_OP_TYPE_RCVBUF   2
#define  SOCK_OP_TYPE_SNDBUF   3
#define  SOCK_OP_TYPE_KEEPALIVE   4
#define  SOCK_OP_TYPE_LINGER   5
#define  SOCK_OP_TYPE_TIMEOUT   6

#define  SOCK_OP_VAL_NON_BLOCK   1
#define  SOCK_OP_VAL_BLOCK   0
#define  SOCK_OP_VAL_NON_REUSEADDR   1
#define  SOCK_OP_VAL_REUSEADDR   0

s32 sock_option(s32 sockfd, s32 opType, s32 opValue, s32 opValue2) {
    s32 ret = 0;
    switch (opType) {
        case SOCK_OP_TYPE_NON_BLOCK: {//阻塞设置

#if __JVM_OS_MINGW__ || __JVM_OS_CYGWIN__ || __JVM_OS_VS__
#if __JVM_OS_CYGWIN__
            __ms_u_long ul = 1;
//fix cygwin bug ,cygwin FIONBIO = 0x8008667E
#undef FIONBIO
#define FIONBIO 0x8004667E
#else
            u_long ul = 1;
#endif
            if (!opValue) {
                ul = 0;
            }
            //jvm_printf(" FIONBIO:%x\n", FIONBIO);
            ret = ioctlsocket(sockfd, FIONBIO, &ul);
            if (ret == SOCKET_ERROR) {
                //err("set socket non_block error: %s\n", strerror(errno));
                s32 ec = WSAGetLastError();
                //jvm_printf(" error code:%d\n", ec);
            }
#else
            if (opValue) {
                s32 flags = fcntl(sockfd, F_GETFL, 0);
                ret = fcntl(sockfd, F_SETFL, flags | O_NONBLOCK);
                if (ret) {
                    //err("set socket non_block error.\n");
                    //printf("errno.%02d is: %s\n", errno, strerror(errno));
                }
            } else {
                //fcntl(sockfd, F_SETFL, O_BLOCK);
            }
#endif
            break;
        }
        case SOCK_OP_TYPE_REUSEADDR: {//
            s32 x = 1;
            ret = setsockopt(sockfd, SOL_SOCKET, SO_REUSEADDR, (char *) &x, sizeof(x));
            break;
        }
        case SOCK_OP_TYPE_RCVBUF: {//缓冲区设置
            int nVal = opValue;//设置为 opValue K
            ret = setsockopt(sockfd, SOL_SOCKET, SO_RCVBUF, (const char *) &nVal, sizeof(nVal));
            break;
        }
        case SOCK_OP_TYPE_SNDBUF: {//缓冲区设置
            s32 nVal = opValue;//设置为 opValue K
            ret = setsockopt(sockfd, SOL_SOCKET, SO_SNDBUF, (const char *) &nVal, sizeof(nVal));
            break;
        }
        case SOCK_OP_TYPE_TIMEOUT: {
#if __JVM_OS_MINGW__ || __JVM_OS_CYGWIN__ || __JVM_OS_VS__
            s32 nTime = opValue;
            ret = setsockopt(sockfd, SOL_SOCKET, SO_RCVTIMEO, (const char *) &nTime, sizeof(nTime));
#else
            struct timeval timeout = {opValue / 1000, (opValue % 1000) * 1000};
            ret = setsockopt(sockfd, SOL_SOCKET, SO_RCVTIMEO, &timeout, sizeof(timeout));
#endif
            break;
        }
        case SOCK_OP_TYPE_LINGER: {
            struct {
                u16 l_onoff;
                u16 l_linger;
            } m_sLinger;
            //(在closesocket()调用,但是还有数据没发送完毕的时候容许逗留)
            // 如果m_sLinger.l_onoff=0;则功能和2.)作用相同;
            m_sLinger.l_onoff = opValue;
            m_sLinger.l_linger = opValue2;//(容许逗留的时间为5秒)
            ret = setsockopt(sockfd, SOL_SOCKET, SO_RCVTIMEO, (const char *) &m_sLinger, sizeof(m_sLinger));
            break;
        }
        case SOCK_OP_TYPE_KEEPALIVE: {
            s32 val = opValue;
            ret = setsockopt(sockfd, SOL_SOCKET, SO_RCVTIMEO, (const char *) &val, sizeof(val));
            break;
        }
    }
    return ret;
}

s32 sock_get_option(s32 sockfd, s32 opType) {
    s32 ret = 0;
    socklen_t len;

    switch (opType) {
        case SOCK_OP_TYPE_NON_BLOCK: {//阻塞设置
#if __JVM_OS_MINGW__ || __JVM_OS_CYGWIN__ || __JVM_OS_VS__
            u_long flags = 1;
            ret = NO_ERROR == ioctlsocket(sockfd, FIONBIO, &flags);
#else
            int flags;
            if ((flags = fcntl(sockfd, F_GETFL, NULL)) < 0) {
                ret = -1;
            } else {
                ret = (flags & O_NONBLOCK);
            }
#endif
            break;
        }
        case SOCK_OP_TYPE_REUSEADDR: {//
            len = sizeof(ret);
            getsockopt(sockfd, SOL_SOCKET, SO_REUSEADDR, (void *) &ret, &len);

            break;
        }
        case SOCK_OP_TYPE_RCVBUF: {
            len = sizeof(ret);
            getsockopt(sockfd, SOL_SOCKET, SO_RCVBUF, (void *) &ret, &len);
            break;
        }
        case SOCK_OP_TYPE_SNDBUF: {//缓冲区设置
            len = sizeof(ret);
            getsockopt(sockfd, SOL_SOCKET, SO_SNDBUF, (void *) &ret, &len);
            break;
        }
        case SOCK_OP_TYPE_TIMEOUT: {

#if __JVM_OS_MINGW__ || __JVM_OS_CYGWIN__ || __JVM_OS_VS__
            len = sizeof(ret);
            getsockopt(sockfd, SOL_SOCKET, SO_RCVTIMEO, (void *) &ret, &len);
#else
            struct timeval timeout = {0, 0};
            len = sizeof(timeout);
            getsockopt(sockfd, SOL_SOCKET, SO_RCVTIMEO, &timeout, &len);
            ret = timeout.tv_sec * 1000 + timeout.tv_usec / 1000;
#endif
            break;
        }
        case SOCK_OP_TYPE_LINGER: {
            struct {
                u16 l_onoff;
                u16 l_linger;
            } m_sLinger;
            //(在closesocket()调用,但是还有数据没发送完毕的时候容许逗留)
            // 如果m_sLinger.l_onoff=0;则功能和2.)作用相同;
            m_sLinger.l_onoff = 0;
            m_sLinger.l_linger = 0;//(容许逗留的时间为5秒)
            len = sizeof(m_sLinger);
            getsockopt(sockfd, SOL_SOCKET, SO_RCVTIMEO, (void *) &m_sLinger, &len);
            ret = *((s32 *) &m_sLinger);
            break;
        }
        case SOCK_OP_TYPE_KEEPALIVE: {
            len = sizeof(ret);
            getsockopt(sockfd, SOL_SOCKET, SO_RCVTIMEO, (void *) &ret, &len);
            break;
        }
    }
    return ret;
}

s32 sock_recv(s32 sockfd, c8 *buf, s32 count) {
    s32 len = (s32) recv(sockfd, buf, count, 0);

    if (len == 0) {//如果是正常断开，返回-1
        len = -1;
    } else if (len == -1) {//如果发生错误
        len = -1;
#if __JVM_OS_VS__ || __JVM_OS_MINGW__ || __JVM_OS_CYGWIN__
        if (WSAEWOULDBLOCK == WSAGetLastError()) {//但是如果是非阻塞端口，说明连接仍正常
            //jvm_printf("sc send error client time = %f ;\n", (f64)clock());
            len = -2;
        }
#else
        if (errno == EWOULDBLOCK || errno == EAGAIN) {
            len = -2;
        }
#endif
    }
    return len;
}


s32 sock_send(s32 sockfd, c8 *buf, s32 count) {
    s32 len = (s32) send(sockfd, buf, count, 0);

    if (len == 0) {//如果是正常断开，返回-1
        len = -1;
    } else if (len == -1) {//如果发生错误
#if __JVM_OS_VS__ || __JVM_OS_MINGW__ || __JVM_OS_CYGWIN__
        if (WSAEWOULDBLOCK == WSAGetLastError()) {//但是如果是非阻塞端口，说明连接仍正常
            //jvm_printf("sc send error server time = %f ;\n", (f64)clock());
            len = -2;
        }
#else
        if (errno == EWOULDBLOCK || errno == EAGAIN) {
            len = -2;
        }
#endif

    }
    return len;
}

s32 sock_open() {
    s32 sockfd = -1;

#if __JVM_OS_VS__ || __JVM_OS_MINGW__ || __JVM_OS_CYGWIN__
    WSADATA wsaData;
    WSAStartup(MAKEWORD(1, 1), &wsaData);
#endif  /*  WIN32  */
    if ((sockfd = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP)) == -1) {
        //err(strerror(errno));
        //err("socket init error: %s\n", strerror(errno));
    }
    return sockfd;
}


s32 sock_connect(s32 sockfd, Utf8String *remote_ip, s32 remote_port) {
    s32 ret = 0;

    struct hostent *host;
    if ((host = gethostbyname(utf8_cstr(remote_ip))) == NULL) { /* get the host info */
        //err("get host by name error: %s\n", strerror(errno));
        ret = -1;
    } else {


        s32 x = 1;
        if (setsockopt(sockfd, SOL_SOCKET, SO_REUSEADDR, (char *) &x, sizeof(x)) == -1) {
            //err("socket reuseaddr error: %s\n", strerror(errno));
            ret = -1;
        } else {
            struct sockaddr_in sock_addr; /* connector's address information */
            memset((char *) &sock_addr, 0, sizeof(sock_addr));
            sock_addr.sin_family = AF_INET; /* host byte order */
            sock_addr.sin_port = htons((u16) remote_port); /* short, network byte order */
#if __JVM_OS_MAC__ || __JVM_OS_LINUX__
            sock_addr.sin_addr = *((struct in_addr *) host->h_addr_list[0]);
#else
            sock_addr.sin_addr = *((struct in_addr *) host->h_addr);
#endif
            memset(&(sock_addr.sin_zero), 0, sizeof((sock_addr.sin_zero))); /* zero the rest of the struct */
            if (connect(sockfd, (struct sockaddr *) &sock_addr, sizeof(sock_addr)) == -1) {
                //err("socket connect error: %s\n", strerror(errno));
                ret = -1;
            }
        }
    }
    return ret;
}

s32 sock_bind(s32 sockfd, Utf8String *local_ip, s32 local_port) {
    s32 ret = 0;
    struct sockaddr_in addr;

    struct hostent *host;

    memset((char *) &addr, 0, sizeof(addr));//清0
    addr.sin_family = AF_INET;
    addr.sin_port = htons(local_port);
    if (local_ip->length) {//如果指定了ip
        if ((host = gethostbyname(utf8_cstr(local_ip))) == NULL) { /* get the host info */
            //err("get host by name error: %s\n", strerror(errno));
            ret = -1;
        }
#if __JVM_OS_VS__ || __JVM_OS_MINGW__ || __JVM_OS_CYGWIN__
        addr.sin_addr = *((struct in_addr *) host->h_addr);
#else
        //server_addr.sin_len = sizeof(struct sockaddr_in);
        addr.sin_addr = *((struct in_addr *) host->h_addr_list[0]);
#endif
    } else {
        addr.sin_addr.s_addr = htonl(INADDR_ANY);
    }

    s32 on = 1;
    setsockopt(sockfd, SOL_SOCKET, SO_REUSEADDR, (char *) &on, sizeof(on));
    if ((bind(sockfd, (struct sockaddr *) &addr, sizeof(addr))) < 0) {
        //err("Error binding serversocket: %s\n", strerror(errno));
        closesocket(sockfd);
        ret = -1;
    }
    return ret;
}


s32 sock_listen(s32 listenfd) {
    u16 MAX_LISTEN = 64;
    if ((listen(listenfd, MAX_LISTEN)) < 0) {
        //err("Error listening on serversocket: %s\n", strerror(errno));
        return -1;
    }
    return 0;
}

s32 sock_accept(s32 listenfd) {
    struct sockaddr_in clt_addr;
    memset(&clt_addr, 0, sizeof(clt_addr)); //清0
    s32 clt_addr_length = sizeof(clt_addr);
    s32 clt_socket_fd = accept(listenfd, (struct sockaddr *) &clt_addr, (socklen_t *) &clt_addr_length);
    if (clt_socket_fd == -1) {
        if (errno != EINTR) {
            //err("Error accepting on serversocket: %s\n", strerror(errno));
        }
    }

    return clt_socket_fd;
}

s32 sock_close(s32 listenfd) {
    shutdown(listenfd, SHUT_RDWR);
    closesocket(listenfd);
#if __JVM_OS_VS__ || __JVM_OS_MINGW__ || __JVM_OS_CYGWIN__
    //can not cleanup , maybe other socket is alive
//        WSACancelBlockingCall();
//        WSACleanup();
#endif

    return 0;
}


s32 host_2_ip4(Utf8String *hostname) {
    s32 addr;

#if __JVM_OS_VS__ || __JVM_OS_MINGW__ || __JVM_OS_CYGWIN__
    WSADATA wsaData;
    WSAStartup(MAKEWORD(1, 1), &wsaData);
#endif  /*  WIN32  */
    struct hostent *host;
    if ((host = gethostbyname(utf8_cstr(hostname))) == NULL) { /* get the host info */
        //err("get host by name error: %s\n", strerror(errno));
        addr = -1;
    }
#if __JVM_OS_MAC__ || __JVM_OS_LINUX__
    addr = ((struct in_addr *) host->h_addr_list[0])->s_addr;
#else
    addr = ((struct in_addr *) host->h_addr)->s_addr;
#endif
    return addr;
}

/**
 * load file less than 4G bytes
 */


s32 isDir(Utf8String *path) {
    struct stat buf;
    stat(utf8_cstr(path), &buf);
    s32 a = S_ISDIR(buf.st_mode);
    return a;
}

s32 jstring_2_utf8(struct java_lang_String *jstr, Utf8String *utf8) {
    if (!jstr)return 1;
    JArray *arr = jstr->value_in_string;
    if (arr) {
        s32 count = jstr->count_in_string;
        s32 offset = jstr->offset_in_string;
        u16 *arrbody = arr->prop.as_u16_arr;
        if (arr->prop.as_u16_arr)unicode_2_utf8(&arrbody[offset], utf8, count);
    }
    return 0;
}

s32 jstring_equals(struct java_lang_String *jstr1, struct java_lang_String *jstr2) {
    if (!jstr1 && !jstr2) { //两个都是null
        return 1;
    } else if (!jstr1) {
        return 0;
    } else if (!jstr2) {
        return 0;
    }
    JArray *arr1 = jstr1->value_in_string;//取得 char[] value
    JArray *arr2 = jstr2->value_in_string;//取得 char[] value
    s32 count1 = 0, offset1 = 0, count2 = 0, offset2 = 0;
    //0长度字符串可能value[] 是空值，也可能不是空值但count是0
    if (arr1) {
        count1 = jstr1->count_in_string;
        offset1 = jstr1->offset_in_string;
    }
    if (arr2) {
        count2 = jstr2->count_in_string;
        offset2 = jstr2->offset_in_string;
    }
    if (count1 != count2) {
        return 0;
    } else if (count1 == 0 && count2 == 0) {
        return 1;
    }
    u16 *jchar_arr1 = arr1->prop.as_u16_arr;
    u16 *jchar_arr2 = arr2->prop.as_u16_arr;
    s32 i;
    for (i = 0; i < count1; i++) {
        if (jchar_arr1[i + offset1] != jchar_arr2[i + offset2]) {
            return 0;
        }
    }
    return 1;
}

void jstring_print(__refer jobj) {
    struct java_lang_String *jstr = (struct java_lang_String *) jobj;
    s32 i;
    for (i = 0; i < jstr->count_in_string; i++) {
        printf("%c", jstr->value_in_string->prop.as_u16_arr[jstr->offset_in_string + i]);
    }
    printf("\n");
}

u16 jstring_char_at(struct java_lang_String *jstr, s32 index) {
    JArray *ptr = (jstr)->value_in_string;
    if (ptr) {
        s32 offset = (jstr)->offset_in_string;
        s32 count = (jstr)->count_in_string;
        if (index >= count) {
            return -1;
        }
        return ptr->prop.as_u16_arr[offset + index];
    }
    return -1;
}


s32 jstring_index_of(struct java_lang_String *jstr, s32 ch, s32 startAt) {
    JArray *ptr = (jstr)->value_in_string;
    if (ptr && startAt >= 0) {
        u16 *jchar_arr = (u16 *) ptr->prop.as_u16_arr;
        s32 count = (jstr)->count_in_string;
        s32 offset = (jstr)->offset_in_string;
        s32 i;
        for (i = startAt; i < count; i++) {
            if (jchar_arr[i + offset] == ch) {
                return i;
            }
        }
    }
    return -1;
}


JObject *buildStackElement(JThreadRuntime *runtime, StackFrame *target) {
    JClass *clazz = get_class_by_name_c(STR_JAVA_LANG_STACKTRACEELEMENT);
    if (clazz) {
        struct java_lang_StackTraceElement *ins = (__refer) new_instance_with_class(runtime, clazz);
        instance_hold_to_thread(runtime, ins);
        instance_init(runtime, (__refer) ins);
        MethodInfo *method = get_methodinfo_by_rawindex(target->methodRawIndex);

        //
        ins->declaringClass_in_stacktraceelement = (__refer) construct_string_with_cstr(runtime, utf8_cstr(method->clazz->name));
        ins->methodName_in_stacktraceelement = (__refer) construct_string_with_cstr(runtime, utf8_cstr(method->name));
        ins->fileName_in_stacktraceelement = (__refer) construct_string_with_cstr(runtime, utf8_cstr(method->clazz->source_name));
        ins->lineNumber_in_stacktraceelement = target->lineNo;
        if (target->next) {
            ins->parent_in_stacktraceelement = (__refer) buildStackElement(runtime, target->next);
        }
        instance_release_from_thread(runtime, ins);
        return (__refer) ins;
    }
    return NULL;
}


Utf8String *getTmpDir() {
    Utf8String *tmps = utf8_create();
#if __JVM_OS_MINGW__ || __JVM_OS_CYGWIN__ || __JVM_OS_VS__
    c8 buf[128];
    s32 len = GetTempPath(128, buf);
    utf8_append_data(tmps, buf, len);
#else

#ifndef P_tmpdir
#define P_tmpdir "/tmp"
#endif
    utf8_append_c(tmps, P_tmpdir);
#endif
    return tmps;
}




//=================================  native ====================================

//native methods
JNIEXPORT s32 JNICALL Java_com_sun_cldc_i18n_mini_Conv_byteToChar__I_3BII_3CII_I(JThreadRuntime *runtime, s32 p0, JArray *p1, s32 p2, s32 p3, JArray *p4, s32 p5, s32 p6) {
    return 0;
}

s32 Java_com_sun_cldc_i18n_mini_Conv_charToByte__I_3CII_3BII_I(JThreadRuntime *runtime, s32 p0, JArray *p1, s32 p2, s32 p3, JArray *p4, s32 p5, s32 p6) {
    return 0;
}

s32 Java_com_sun_cldc_i18n_mini_Conv_getByteLength__I_3BII_I(JThreadRuntime *runtime, s32 p0, JArray *p1, s32 p2, s32 p3) {
    return 0;
}

s32 Java_com_sun_cldc_i18n_mini_Conv_getHandler__Ljava_lang_String_2_I(JThreadRuntime *runtime, struct java_lang_String *p0) {
    return 0;
}

s32 Java_com_sun_cldc_i18n_mini_Conv_getMaxByteLength__I_I(JThreadRuntime *runtime, s32 p0) {
    return 0;
}

s32 Java_com_sun_cldc_i18n_mini_Conv_sizeOfByteInUnicode__I_3BII_I(JThreadRuntime *runtime, s32 p0, JArray *p1, s32 p2, s32 p3) {
    return 0;
}

s32 Java_com_sun_cldc_i18n_mini_Conv_sizeOfUnicodeInByte__I_3CII_I(JThreadRuntime *runtime, s32 p0, JArray *p1, s32 p2, s32 p3) {
    return 0;
}

s32 Java_com_sun_cldc_io_ConsoleInputStream_read___I(JThreadRuntime *runtime, struct com_sun_cldc_io_ConsoleInputStream *p0) {
    return getchar();
}

void Java_com_sun_cldc_io_ConsoleOutputStream_write__I_V(JThreadRuntime *runtime, struct com_sun_cldc_io_ConsoleOutputStream *p0, s32 p1) {
    fprintf(stdout, "%c", (s8) p1);
}

JArray *Java_com_sun_cldc_io_ResourceInputStream_open__Ljava_lang_String_2__3B(JThreadRuntime *runtime, struct java_lang_String *p0) {
    Utf8String *path = utf8_create();
    jstring_2_utf8(p0, path);
    c8 *home = "./";//glfmGetResRoot();
    Utf8String *cache = tss_get(TLS_KEY_UTF8STR_CACHE);
    utf8_clear(cache);
    utf8_append_c(cache, home);
    utf8_pushback(cache, '/');
    utf8_append(cache, path);
    jvm_printf("open file :%s\n", utf8_cstr(cache));
    ByteBuf *buf = load_file_from_classpath(cache);
    utf8_destory(path);
    if (buf) {
        s32 _j_t_bytes = buf->wp;
        JArray *_arr = multi_array_create_by_typename(runtime, &_j_t_bytes, 1, "[B");
        bytebuf_read_batch(buf, _arr->prop.as_s8_arr, _j_t_bytes);
        bytebuf_destory(buf);
        return _arr;
    } else {
        return NULL;
    }
}

void Java_com_sun_cldc_io_Waiter_waitForIO___V(JThreadRuntime *runtime) {
    return;
}

struct java_lang_Class *Java_java_lang_ClassLoader_getCaller___Ljava_lang_Class_2(JThreadRuntime *runtime) {
    return NULL;
}

void Java_java_lang_ClassLoader_load__Ljava_lang_String_2Ljava_lang_Class_2Z_V(JThreadRuntime *runtime, struct java_lang_String *p0, struct java_lang_Class *p1, s8 p2) {
    return;
}

struct java_lang_Class *Java_java_lang_Class_forName__Ljava_lang_String_2ZLjava_lang_ClassLoader_2_Ljava_lang_Class_2(JThreadRuntime *runtime, struct java_lang_String *p0, s8 p1, struct java_lang_ClassLoader *p2) {
    JClass *cl = NULL;
    if (p0) {
        Utf8String *ustr = utf8_create();
        jstring_2_utf8(p0, ustr);
        utf8_replace_c(ustr, ".", "/");
        class_clinit(runtime, ustr);
        cl = get_class_by_name(ustr);
        utf8_destory(ustr);
        if (!cl) {
            JObject *exception = new_instance_with_name(runtime, STR_JAVA_LANG_CLASS_NOT_FOUND_EXCEPTION);
            instance_init(runtime, exception);
            throw_exception(runtime, exception);
        } else {
            JObject *ins = ins_of_Class_create_get(runtime, cl);
            return (__refer) ins;
        }
    } else {
        JObject *exception = new_instance_with_name(runtime, STR_JAVA_LANG_NULL_POINTER_EXCEPTION);
        instance_init(runtime, exception);
        throw_exception(runtime, exception);
    }
    return NULL;
}

struct java_lang_ClassLoader *Java_java_lang_Class_getClassLoader0___Ljava_lang_ClassLoader_2(JThreadRuntime *runtime, struct java_lang_Class *p0) {
    return NULL;
}

struct java_lang_String *Java_java_lang_Class_getName0___Ljava_lang_String_2(JThreadRuntime *runtime, struct java_lang_Class *p0) {
    JClass *cl = (__refer) (intptr_t) p0->classHandle_in_class;
    if (cl) {
        Utf8String *ustr = utf8_create_copy(cl->name);
        utf8_replace_c(ustr, "/", ".");
        JObject *ins = construct_string_with_cstr(runtime, utf8_cstr(ustr));
        utf8_destory(ustr);
        return (__refer) ins;
    } else {
        return NULL;
    }
}

struct java_lang_Class *Java_java_lang_Class_getPrimitiveClass__Ljava_lang_String_2_Ljava_lang_Class_2(JThreadRuntime *runtime, struct java_lang_String *p0) {
    if (p0) {
        Utf8String *ustr = utf8_create();
        jstring_2_utf8(p0, ustr);
        JClass *cl = primitive_class_create_get(runtime, ustr);
        utf8_destory(ustr);
        return (__refer) cl->ins_of_Class;
    } else {
        return NULL;
    }
}

struct java_lang_Class *Java_java_lang_Class_getSuperclass___Ljava_lang_Class_2(JThreadRuntime *runtime, struct java_lang_Class *p0) {
    if (p0) {
        JClass *scl = getSuperClass((__refer) (intptr_t) p0->classHandle_in_class);
        if (!scl) return NULL;
        JObject *ins = ins_of_Class_create_get(runtime, scl);
        return (__refer) ins;
    } else {
        return NULL;
    }
}

s8 Java_java_lang_Class_isArray___Z(JThreadRuntime *runtime, struct java_lang_Class *p0) {
    JClass *clazz = ((JClass *) (__refer) (intptr_t) p0->classHandle_in_class);
    return clazz->array_cell_class != NULL;
}

s8 Java_java_lang_Class_isAssignableFrom__Ljava_lang_Class_2_Z(JThreadRuntime *runtime, struct java_lang_Class *p0, struct java_lang_Class *p1) {
    JClass *c0 = (__refer) (intptr_t) p0->classHandle_in_class;
    JClass *c1 = (__refer) (intptr_t) p1->classHandle_in_class;

    return assignable_from(c1, c0);
}

s8 Java_java_lang_Class_isInstance__Ljava_lang_Object_2_Z(JThreadRuntime *runtime, struct java_lang_Class *p0, struct java_lang_Object *p1) {
    return instance_of((InstProp *) p1, (__refer) (intptr_t) p0->classHandle_in_class);
}

s8 Java_java_lang_Class_isInterface___Z(JThreadRuntime *runtime, struct java_lang_Class *p0) {
    ClassRaw *raw = ((JClass *) (__refer) (intptr_t) p0->classHandle_in_class)->raw;
    if (raw)return (s8) (raw->acc_flag & ACC_INTERFACE);
    return 0;//array
}

s8 Java_java_lang_Class_isPrimitive___Z(JThreadRuntime *runtime, struct java_lang_Class *p0) {
    JClass *clazz = ((JClass *) (__refer) (intptr_t) p0->classHandle_in_class);
    return (s8) (clazz->primitive);
}

struct java_lang_Object *Java_java_lang_Class_newInstance___Ljava_lang_Object_2(JThreadRuntime *runtime, struct java_lang_Class *p0) {
    JClass *cl = ((JClass *) (__refer) (intptr_t) p0->classHandle_in_class);
    if (cl && !cl->prop.arr_type) {//class exists and not array class
        JObject *ins = new_instance_with_class(runtime, cl);
        instance_init(runtime, ins);
        return (__refer) ins;
    } else {
        JObject *exception = new_instance_with_name(runtime, STR_JAVA_LANG_INSTANTIATION_EXCEPTION);
        instance_init(runtime, exception);
        throw_exception(runtime, exception);
    }
    return NULL;
}

s64 Java_java_lang_Double_doubleToLongBits__D_J(JThreadRuntime *runtime, f64 p0) {
    StackItem si;
    si.d = p0;
    return si.j;
}

f64 Java_java_lang_Double_longBitsToDouble__J_D(JThreadRuntime *runtime, s64 p0) {
    StackItem si;
    si.j = p0;
    return si.d;
}

s32 Java_java_lang_Float_floatToIntBits__F_I(JThreadRuntime *runtime, f32 p0) {
    StackItem si;
    si.f = p0;
    return si.i;
}

f32 Java_java_lang_Float_intBitsToFloat__I_F(JThreadRuntime *runtime, s32 p0) {
    StackItem si;
    si.i = p0;
    return si.f;
}

f64 Java_java_lang_Math_acos__D_D(JThreadRuntime *runtime, f64 p0) {
    return acos(p0);
}

f64 Java_java_lang_Math_asin__D_D(JThreadRuntime *runtime, f64 p0) {
    return asin(p0);
}

f64 Java_java_lang_Math_atan__D_D(JThreadRuntime *runtime, f64 p0) {
    return atan(p0);
}

f64 Java_java_lang_Math_atan2__DD_D(JThreadRuntime *runtime, f64 p0, f64 p1) {
    return atan2(p0, p1);
}

f64 Java_java_lang_Math_ceil__D_D(JThreadRuntime *runtime, f64 p0) {
    return ceil(p0);
}

f64 Java_java_lang_Math_cos__D_D(JThreadRuntime *runtime, f64 p0) {
    return cos(p0);
}

f64 Java_java_lang_Math_exp__D_D(JThreadRuntime *runtime, f64 p0) {
    return exp(p0);
}

f64 Java_java_lang_Math_floor__D_D(JThreadRuntime *runtime, f64 p0) {
    return floor(p0);
}

f64 Java_java_lang_Math_log__D_D(JThreadRuntime *runtime, f64 p0) {
    return log(p0);
}

f64 Java_java_lang_Math_pow__DD_D(JThreadRuntime *runtime, f64 p0, f64 p1) {
    return pow(p0, p1);
}

f64 Java_java_lang_Math_sin__D_D(JThreadRuntime *runtime, f64 p0) {
    return sin(p0);
}

f64 Java_java_lang_Math_sqrt__D_D(JThreadRuntime *runtime, f64 p0) {
    return sqrt(p0);
}

f64 Java_java_lang_Math_tan__D_D(JThreadRuntime *runtime, f64 p0) {
    return tan(p0);
}

struct java_lang_Object *Java_java_lang_Object_clone___Ljava_lang_Object_2(JThreadRuntime *runtime, struct java_lang_Object *p0) {
    return (__refer) instance_copy(runtime, (InstProp *) p0, 0);
}

struct java_lang_Class *Java_java_lang_Object_getClass___Ljava_lang_Class_2(JThreadRuntime *runtime, struct java_lang_Object *p0) {
    return (__refer) ins_of_Class_create_get(runtime, ((InstProp *) p0)->clazz);
}

s32 Java_java_lang_Object_hashCode___I(JThreadRuntime *runtime, struct java_lang_Object *p0) {
    u64 a = (u64) (intptr_t) p0;
    s32 h = (s32) (a ^ (a >> 32));
    return h;
}

void Java_java_lang_Object_notify___V(JThreadRuntime *runtime, struct java_lang_Object *p0) {
    jthread_notify((InstProp *) p0);
}

void Java_java_lang_Object_notifyAll___V(JThreadRuntime *runtime, struct java_lang_Object *p0) {
    jthread_notifyAll((InstProp *) p0);
}

void Java_java_lang_Object_wait__J_V(JThreadRuntime *runtime, struct java_lang_Object *ins, s64 t) {
    jthread_waitTime((InstProp *) ins, runtime, t);
}

void Java_java_lang_Runtime_exitInternal__I_V(JThreadRuntime *runtime, struct java_lang_Runtime *p0, s32 p1) {
    return;
}

s64 Java_java_lang_Runtime_freeMemory___J(JThreadRuntime *runtime, struct java_lang_Runtime *p0) {
    return g_jvm->collector->MAX_HEAP_SIZE - g_jvm->collector->heap_size;
}

void Java_java_lang_Runtime_gc___V(JThreadRuntime *runtime, struct java_lang_Runtime *p0) {
    return;
}

s64 Java_java_lang_Runtime_totalMemory___J(JThreadRuntime *runtime, struct java_lang_Runtime *p0) {
    return g_jvm->collector->MAX_HEAP_SIZE;
}

//struct java_lang_StringBuilder *Java_java_lang_StringBuilder_append__Ljava_lang_String_2_Ljava_lang_StringBuilder_2(struct java_lang_StringBuilder *p0, struct java_lang_String *p1) {
//    struct java_lang_StringBuilder *jbuilder = p0;
//    struct java_lang_String *jstr = p1;
//
//    if (jstr) {
//        s32 scount = jstr->count_2;
//        if (scount) {
//            JArray *bvalue = jbuilder->value_0;
//            s32 bcount = jbuilder->count_1;
//
//            s32 soffset = jstr->offset_1;
//            JArray *svalue = jstr->value_0;
//            s32 bytes = data_type_bytes[DATATYPE_JCHAR];
//            if (bvalue->prop.arr_length - bcount < scount) {//need expand stringbuilder
//                s32 n_count = bcount + scount + 1;
//                n_count = n_count > bcount * 2 ? n_count : bcount * 2;
//                JArray *b_new_v = multi_array_create_by_typename(&n_count, 1, "[C");
//                memcpy(b_new_v->prop.as_s8_arr, bvalue->prop.as_s8_arr, bcount * bytes);
//                jbuilder->value_0 = b_new_v;
//                bvalue = b_new_v;
//            }
//            c8 *b_body = bvalue->prop.as_s8_arr + (bcount * bytes);
//            c8 *s_body = svalue->prop.as_s8_arr + (soffset * bytes);
//            memcpy(b_body, s_body, scount * bytes);
//            jbuilder->count_1 = bcount + scount;
//        }
//    }
//    return jbuilder;
//}

u16 Java_java_lang_String_charAt0__I_C(JThreadRuntime *runtime, struct java_lang_String *p0, s32 p1) {
    JArray *carr = p0->value_in_string;
    s32 offset = p0->offset_in_string;
    s32 count = p0->count_in_string;
    if (p1 >= count) {
        JObject *exception = new_instance_with_name(runtime, STR_JAVA_LANG_NULL_POINTER_EXCEPTION);
        instance_init(runtime, exception);
        runtime->exception = exception;
        return 0;
    }
    return carr->prop.as_u16_arr[p1 + offset];
}

s8 Java_java_lang_String_equals__Ljava_lang_Object_2_Z(JThreadRuntime *runtime, struct java_lang_String *p0, struct java_lang_Object *p1) {
    return jstring_equals(p0, (struct java_lang_String *) p1);
}

s32 Java_java_lang_String_indexOf__I_I(JThreadRuntime *runtime, struct java_lang_String *p0, s32 p1) {
    return jstring_index_of(p0, p1, 0);
}

s32 Java_java_lang_String_indexOf__II_I(JThreadRuntime *runtime, struct java_lang_String *p0, s32 p1, s32 p2) {
    return jstring_index_of(p0, p1, p2);
}

struct java_lang_String *Java_java_lang_String_intern0___Ljava_lang_String_2(JThreadRuntime *runtime, struct java_lang_String *p0) {
    Utf8String *ustr = utf8_create();
    jstring_2_utf8(p0, ustr);
    if (!ustr)return NULL;
    JObject *in_jstr = hashtable_get(g_jvm->table_jstring_const, ustr);
    if (!in_jstr) {
        in_jstr = construct_string_with_cstr(runtime, utf8_cstr(ustr));
        hashtable_put(g_jvm->table_jstring_const, ustr, in_jstr);
    }
    return (__refer) in_jstr;
}

JArray *Java_java_lang_String_replace0__Ljava_lang_String_2Ljava_lang_String_2__3C(JThreadRuntime *runtime, struct java_lang_String *p0, struct java_lang_String *p1, struct java_lang_String *p2) {

    s32 count = p0->count_in_string;
    s32 offset = p0->offset_in_string;
    u16 *value = p0->value_in_string->prop.as_u16_arr;

    s32 src_count = p1->count_in_string;
    s32 dst_count = p2->count_in_string;
    if (count == 0 || p1 == NULL || p2 == NULL || src_count == 0 || dst_count == 0) {
        JArray *jchar_arr = multi_array_create_by_typename(runtime, &count, 1, "[C");
        memcpy((c8 *) jchar_arr->prop.as_s8_arr, (c8 *) &value[offset], count * sizeof(u16));
        return jchar_arr;
    } else {

        s32 src_offset = p1->offset_in_string;
        u16 *src_value = p1->value_in_string->prop.as_u16_arr;
        s32 dst_offset = p2->offset_in_string;
        u16 *dst_value = p2->value_in_string->prop.as_u16_arr;

        ByteBuf *sb = bytebuf_create(count);
        int i, j;
        for (i = 0; i < count;) {
            int index = i + offset;
            u16 ch = value[index];
            s32 match = 0;
            if (ch == src_value[src_offset]) {
                match = 1;
                for (j = 1; j < src_count; j++) {
                    if (value[index + j] != src_value[src_offset + j]) {
                        match = 0;
                        break;
                    }
                }
            }
            if (match) {
                bytebuf_write_batch(sb, (c8 *) &dst_value[dst_offset], dst_count * sizeof(ch));
                i += src_count;
            } else {
                bytebuf_write_batch(sb, (c8 *) &ch, sizeof(ch));
                i++;
            }
        }
        s32 jchar_count = sb->wp / 2;
        JArray *jchar_arr = multi_array_create_by_typename(runtime, &jchar_count, 1, "[C");
        bytebuf_read_batch(sb, jchar_arr->prop.as_s8_arr, sb->wp);
        bytebuf_destory(sb);
        return jchar_arr;
    }

    return NULL;
}

void Java_java_lang_System_arraycopy__Ljava_lang_Object_2ILjava_lang_Object_2II_V(JThreadRuntime *runtime, struct java_lang_Object *src, s32 srcPos, struct java_lang_Object *dst, s32 dstPos, s32 len) {
    if (src == NULL || dst == NULL) {
        JObject *exception = new_instance_with_name(runtime, STR_JAVA_LANG_NULL_POINTER_EXCEPTION);
        instance_init(runtime, exception);
        runtime->exception = exception;
        return;
    } else if (src->prop.type != INS_TYPE_ARRAY
               || dst->prop.type != src->prop.type) {
        JObject *exception = new_instance_with_name(runtime, STR_JAVA_LANG_ILLEGAL_ARGUMENT_EXCEPTION);
        instance_init(runtime, exception);
        runtime->exception = exception;
        return;
    } else if (srcPos < 0 || srcPos + len > src->prop.arr_length
               || dstPos < 0 || dstPos + len > dst->prop.arr_length
            ) {
        JObject *exception = new_instance_with_name(runtime, STR_JAVA_LANG_ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION);
        instance_init(runtime, exception);
        runtime->exception = exception;
        return;
    } else {
        s32 bytes = data_type_bytes[src->prop.clazz->array_cell_type];
        memmove(&dst->prop.as_s8_arr[dstPos * bytes], &src->prop.as_s8_arr[srcPos * bytes], len * bytes);
    }
}

s64 Java_java_lang_System_currentTimeMillis___J(JThreadRuntime *runtime) {
    return currentTimeMillis();
}

struct java_lang_String *Java_java_lang_System_doubleToString__D_Ljava_lang_String_2(JThreadRuntime *runtime, f64 p0) {
    c8 buf[32];
    sprintf(buf, "%lf", p0);
    JObject *jstr = construct_string_with_cstr(runtime, buf);
    return (__refer) jstr;
}

struct java_lang_String *Java_java_lang_System_getClassPath___Ljava_lang_String_2(JThreadRuntime *runtime) {
    return NULL;
}

struct java_lang_String *Java_java_lang_System_getProperty0__Ljava_lang_String_2_Ljava_lang_String_2(JThreadRuntime *runtime, struct java_lang_String *p0) {
    Utf8String *key = utf8_create();
    jstring_2_utf8(p0, key);
    Utf8String *val = (Utf8String *) hashtable_get(g_jvm->sys_prop, key);
    utf8_destory(key);
    if (val) {
        JObject *jstr = construct_string_with_cstr(runtime, utf8_cstr(val));
        return (__refer) jstr;
    }
    return NULL;
}

s32 Java_java_lang_System_identityHashCode__Ljava_lang_Object_2_I(JThreadRuntime *runtime, struct java_lang_Object *p0) {
    u64 a = (u64) (intptr_t) p0;
    s32 h = (s32) (a ^ (a >> 32));
    return h;
}

typedef void (*jni_fun)(__refer);

void Java_java_lang_System_loadLibrary0___3B_V(JThreadRuntime *runtime, JArray *p0) {
    if (p0 && p0->prop.arr_length) {
        Utf8String *lab = utf8_create_c("java.library.path");
        Utf8String *val = hashtable_get(g_jvm->sys_prop, lab);
        Utf8String *libname = utf8_create();
        if (val) {
            utf8_append(libname, val);
        }
        const c8 *note1 = "lib not found:%s, %s\n";
        const c8 *note2 = "register function not found:%s\n";
        const c8 *onload = "JNI_OnLoad";
        jni_fun f;
#if defined(__JVM_OS_MINGW__) || defined(__JVM_OS_CYGWIN__) || defined(__JVM_OS_VS__)
        utf8_append_c(libname, "/lib");
        utf8_append_c(libname, p0->prop.as_c8_arr);
        utf8_append_c(libname, ".dll");
        utf8_replace_c(libname, "//", "/");
        HINSTANCE hInstLibrary = LoadLibrary(utf8_cstr(libname));
        if (!hInstLibrary) {
            jvm_printf(note1, utf8_cstr(libname));
        } else {
            FARPROC fp = GetProcAddress(hInstLibrary, onload);
            if (!fp) {
                jvm_printf(note2, onload);
            } else {
                f = (jni_fun) fp;
                f(&g_jvm->env);
            }
        }

#else
        utf8_append_c(libname, "/lib");
        utf8_replace_c(libname, "//", "/");
        utf8_append_c(libname, p0->prop.as_s8_arr);
#if defined(__JVM_OS_MAC__)
        utf8_append_c(libname, ".dylib");
#else //__JVM_OS_LINUX__
        utf8_append_c(libname, ".so");
#endif
        __refer lib = dlopen(utf8_cstr(libname), RTLD_LAZY);
        if (!lib) {
            jvm_printf(note1, utf8_cstr(libname), dlerror());
        } else {

            f = dlsym(lib, onload);
            if (!f) {
                jvm_printf(note2, onload);
            } else {
                f(&g_jvm->env);
            }
        }

#endif
        utf8_destory(lab);
        utf8_destory(libname);
    }

}

s64 Java_java_lang_System_nanoTime___J(JThreadRuntime *runtime) {
    return nanoTime();
}

struct java_lang_String *Java_java_lang_System_setProperty0__Ljava_lang_String_2Ljava_lang_String_2_Ljava_lang_String_2(JThreadRuntime *runtime, struct java_lang_String *p0, struct java_lang_String *p1) {

    Utf8String *key = utf8_create();
    jstring_2_utf8(p0, key);
    Utf8String *val = utf8_create();
    jstring_2_utf8(p1, val);
    Utf8String *old_val = (Utf8String *) hashtable_get(g_jvm->sys_prop, key);
    hashtable_put(g_jvm->sys_prop, key, val);
    __refer jstr = NULL;
    if (old_val) {
        jstr = construct_string_with_cstr(runtime, utf8_cstr(old_val));
    }
    return jstr;
}

s32 Java_java_lang_Thread_activeCount___I(JThreadRuntime *runtime) {
    return g_jvm->thread_list->length;
}

struct java_lang_ClassLoader *Java_java_lang_Thread_getContextClassLoader___Ljava_lang_ClassLoader_2(JThreadRuntime *runtime, struct java_lang_Thread *p0) {
    JThreadRuntime *tr = (JThreadRuntime *) (intptr_t) p0->stackFrame_in_thread;
    return (struct java_lang_ClassLoader *) tr->context_classloader;
}

struct java_lang_Thread *Java_java_lang_Thread_currentThread___Ljava_lang_Thread_2(JThreadRuntime *runtime) {
    return (__refer) runtime->jthread;
}

void Java_java_lang_Thread_interrupt0___V(JThreadRuntime *runtime, struct java_lang_Thread *p0) {
    return;
}

s8 Java_java_lang_Thread_isAlive___Z(JThreadRuntime *runtime, struct java_lang_Thread *p0) {
    s32 i, imax;
    spin_lock(&g_jvm->thread_list->spinlock);
    for (i = 0, imax = g_jvm->thread_list->length; i < imax; i++) {
        JThreadRuntime *runtime = arraylist_get_value_unsafe(g_jvm->thread_list, i);
        if (runtime->jthread == (__refer) p0) {
            return runtime->thread_status != THREAD_STATUS_DEAD;
        }
    }
    spin_unlock(&g_jvm->thread_list->spinlock);
    return 0;
}

void Java_java_lang_Thread_setContextClassLoader__Ljava_lang_ClassLoader_2_V(JThreadRuntime *runtime, struct java_lang_Thread *p0, struct java_lang_ClassLoader *p1) {
    JThreadRuntime *tr = (JThreadRuntime *) (intptr_t) p0->stackFrame_in_thread;
    tr->context_classloader = (JObject *) p1;
    return;
}

void Java_java_lang_Thread_setPriority0__I_V(JThreadRuntime *runtime, struct java_lang_Thread *p0, s32 p1) {
    p0->priority_0 = p1;
    return;
}

void Java_java_lang_Thread_sleep__J_V(JThreadRuntime *runtime, s64 t) {
    jthread_sleep(runtime, t);
}

void Java_java_lang_Thread_start___V(JThreadRuntime *runtime, struct java_lang_Thread *ins) {
    jthread_start((JObject *) ins);
}

void Java_java_lang_Thread_yield___V(JThreadRuntime *runtime) {
    jthread_yield();
}

struct java_lang_StackTraceElement *Java_java_lang_Throwable_buildStackElement___Ljava_lang_StackTraceElement_2(JThreadRuntime *runtime, struct java_lang_Throwable *p0) {
    return (__refer) buildStackElement(runtime, runtime->tail);
}

JArray *Java_org_mini_crypt_XorCrypt_decrypt___3B_3B__3B(JThreadRuntime *runtime, JArray *p0, JArray *p1) {
    if (p0 && p1) {
        JArray *r = multi_array_create_by_typename(runtime, &p0->prop.arr_length, 1, "[B");
        s32 i, j, imax;
        for (i = 0, imax = p0->prop.arr_length; i < imax; i++) {
            u32 v = p0->prop.as_s8_arr[i] & 0xff;
            for (j = p1->prop.arr_length - 1; j >= 0; j--) {
                u32 k = p1->prop.as_s8_arr[j] & 0xff;
                v = (v ^ k) & 0xff;

                u32 bitshift = k % 8;

                u32 v1 = (v >> bitshift);
                u32 v2 = (v << (8 - bitshift));
                v = (v1 | v2);

            }
            r->prop.as_s8_arr[i] = v & 0xff;
        }
        return r;
    }
    return NULL;
}

JArray *Java_org_mini_crypt_XorCrypt_encrypt___3B_3B__3B(JThreadRuntime *runtime, JArray *p0, JArray *p1) {
    if (p0 && p1) {
        JArray *r = multi_array_create_by_typename(runtime, &p0->prop.arr_length, 1, "[B");
        s32 i, j, imax, jmax;
        for (i = 0, imax = p0->prop.arr_length; i < imax; i++) {
            u32 v = p0->prop.as_s8_arr[i] & 0xff;
            for (j = 0, jmax = p1->prop.arr_length; j < jmax; j++) {
                u32 k = p1->prop.as_s8_arr[j] & 0xff;

                u32 bitshift = k % 8;

                u32 v1 = (v << bitshift);
                u32 v2 = (v >> (8 - bitshift));
                v = (v1 | v2);

                v = (v ^ k) & 0xff;
            }
            r->prop.as_s8_arr[i] = v & 0xff;
        }
        return r;
    }
    return NULL;
}

s32 Java_org_mini_fs_InnerFile_available0__J_I(JThreadRuntime *runtime, s64 p0) {
    FILE *fd = (FILE *) (intptr_t) p0;

    s32 cur = 0, end = 0;
    if (fd) {
        cur = ftell(fd);
        fseek(fd, (long) 0, SEEK_END);
        end = ftell(fd);
        fseek(fd, (long) cur, SEEK_SET);
    }
    return end - cur;
}

s32 Java_org_mini_fs_InnerFile_chmod___3BI_I(JThreadRuntime *runtime, JArray *p0, s32 p1) {
    if (p0) {
        return chmod(p0->prop.as_s8_arr, p1);
    }
    return -1;
}

s32 Java_org_mini_fs_InnerFile_closeFile__J_I(JThreadRuntime *runtime, s64 p0) {
    FILE *fd = (FILE *) (intptr_t) p0;
    s32 ret = -1;
    if (fd) {
        ret = fclose(fd);
    }
    return ret;
}

s32 Java_org_mini_fs_InnerFile_delete0___3B_I(JThreadRuntime *runtime, JArray *p0) {
    s32 ret = -1;
    if (p0) {
        struct stat buf;
        stat(p0->prop.as_s8_arr, &buf);
        s32 a = S_ISDIR(buf.st_mode);
        if (a) {
            ret = rmdir(p0->prop.as_s8_arr);
        } else {
            ret = remove(p0->prop.as_s8_arr);
        }
    }
    return ret;
}

s32 Java_org_mini_fs_InnerFile_flush0__J_I(JThreadRuntime *runtime, s64 p0) {
    FILE *fd = (FILE *) (intptr_t) p0;
    s32 ret = -1;
    if (fd) {
        ret = fflush(fd);
    }
    return ret;
}

s32 Java_org_mini_fs_InnerFile_fullpath___3B_3B_I(JThreadRuntime *runtime, JArray *p0, JArray *p1) {
    return 0;
}

s32 Java_org_mini_fs_InnerFile_getOS___I(JThreadRuntime *runtime) {
#if __JVM_OS_VS__ || __JVM_OS_MINGW__ || __JVM_OS_CYGWIN__
    return 1;
#else
    return 0;
#endif
}

struct java_lang_String *Java_org_mini_fs_InnerFile_getTmpDir___Ljava_lang_String_2(JThreadRuntime *runtime) {
    Utf8String *tdir = getTmpDir();
    if (tdir) {
        JObject *jstr = construct_string_with_cstr(runtime, utf8_cstr(tdir));
        utf8_destory(tdir);
        return (__refer) jstr;
    }
    return NULL;
}

s32 Java_org_mini_fs_InnerFile_getcwd___3B_I(JThreadRuntime *runtime, JArray *p0) {
    if (p0) {
        __refer ret = getcwd(p0->prop.as_s8_arr, p0->prop.arr_length);
        return ret == p0->prop.as_s8_arr ? 0 : -1;
    }
    return -1;
}

JArray *Java_org_mini_fs_InnerFile_listDir___3B__3Ljava_lang_String_2(JThreadRuntime *runtime, JArray *p0) {
    if (p0) {
        Utf8String *filepath = utf8_create_part_c(p0->prop.as_s8_arr, 0, p0->prop.arr_length);

        DIR *dirp;
        struct dirent *dp;
        dirp = opendir(utf8_cstr(filepath)); //打开目录指针
        utf8_destory(filepath);
        if (dirp) {
            ArrayList *files = arraylist_create(0);
            while ((dp = readdir(dirp)) != NULL) { //通过目录指针读目录
                if (strcmp(dp->d_name, ".") == 0) {
                    continue;
                }
                if (strcmp(dp->d_name, "..") == 0) {
                    continue;
                }
                Utf8String *ustr = utf8_create_c(dp->d_name);
                JObject *jstr = construct_string_with_cstr(runtime, utf8_cstr(ustr));
                instance_hold_to_thread(runtime, jstr);
                utf8_destory(ustr);
                arraylist_push_back(files, jstr);
            }
            (void) closedir(dirp); //关闭目录

            s32 i;
            JArray *jarr = multi_array_create_by_typename(runtime, &files->length, 1, "[Ljava/lang/String;");
            for (i = 0; i < files->length; i++) {
                __refer ref = arraylist_get_value(files, i);
                instance_release_from_thread(runtime, ref);
                jarr->prop.as_obj_arr[i] = ref;
            }
            arraylist_destory(files);
            return jarr;
        }
    }
    return NULL;
}

s32 Java_org_mini_fs_InnerFile_loadFS___3BLorg_mini_fs_InnerFileStat_2_I(JThreadRuntime *runtime, JArray *p0, struct org_mini_fs_InnerFileStat *p1) {
    s32 ret = -1;
    if (p0) {
        Utf8String *filepath = utf8_create_part_c(p0->prop.as_s8_arr, 0, p0->prop.arr_length);
        struct stat buf;
        ret = stat(utf8_cstr(filepath), &buf);
        utf8_destory(filepath);
        s32 a = S_ISDIR(buf.st_mode);
        if (ret == 0) {
            p1->st_1dev_15 = buf.st_dev;
            p1->st_1ino_16 = buf.st_ino;
            p1->st_1mode_17 = buf.st_mode;
            p1->st_1nlink_18 = buf.st_nlink;
            p1->st_1uid_19 = buf.st_uid;
            p1->st_1gid_20 = buf.st_gid;
            p1->st_1rdev_21 = buf.st_rdev;
            p1->st_1size_22 = buf.st_size;
            p1->st_1atime_23 = buf.st_atime;
            p1->st_1mtime_24 = buf.st_mtime;
            p1->st_1ctime_25 = buf.st_ctime;
            p1->exists_14 = 1;
        }
    }
    return ret;
}

s32 Java_org_mini_fs_InnerFile_mkdir0___3B_I(JThreadRuntime *runtime, JArray *p0) {
    s32 ret = -1;
    if (p0) {
#if __JVM_OS_MINGW__ || __JVM_OS_CYGWIN__ || __JVM_OS_VS__
        ret = mkdir(p0->prop.as_s8_arr);
#else
        ret = mkdir(p0->prop.as_s8_arr, S_IRWXU | S_IRWXG | S_IROTH | S_IXOTH);
#endif
    }
    return ret;
}

s64 Java_org_mini_fs_InnerFile_openFile___3B_3B_J(JThreadRuntime *runtime, JArray *p0, JArray *p1) {
    if (p0 && p1) {
        FILE *fd = fopen(p0->prop.as_s8_arr, p1->prop.as_s8_arr);
        return (s64) (intptr_t) fd;
    }
    return 0;
}

s32 Java_org_mini_fs_InnerFile_read0__J_I(JThreadRuntime *runtime, s64 p0) {
    FILE *fd = (FILE *) (intptr_t) p0;
    s32 ret = -1;
    if (fd) {
        ret = fgetc(fd);
        if (ret == EOF) {
            return -1;
        }
    }
    return ret;
}

s32 Java_org_mini_fs_InnerFile_readbuf__J_3BII_I(JThreadRuntime *runtime, s64 p0, JArray *p1, s32 p2, s32 p3) {
    FILE *fd = (FILE *) (intptr_t) p0;
    s32 ret = -1;
    if (fd && p1) {
        ret = (s32) fread(p1->prop.as_s8_arr + p2, 1, p3, fd);
    }
    if (ret == 0) {
        ret = -1;
    }
    return ret;
}

s32 Java_org_mini_fs_InnerFile_rename0___3B_3B_I(JThreadRuntime *runtime, JArray *p0, JArray *p1) {
    if (p0 && p1) {
        return rename(p0->prop.as_s8_arr, p1->prop.as_s8_arr);
    }
    return -1;
}

s32 Java_org_mini_fs_InnerFile_seek0__JJ_I(JThreadRuntime *runtime, s64 p0, s64 p1) {
    FILE *fd = (FILE *) (intptr_t) p0;
    if (fd) {
        return fseek(fd, (long) p1, SEEK_SET);
    }
    return -1;
}

s32 Java_org_mini_fs_InnerFile_setLength0__JJ_I(JThreadRuntime *runtime, s64 p0, s64 p1) {
    FILE *fd = (FILE *) (intptr_t) p0;
    s64 filelen = p1;
    s32 ret = 0;
    if (fd) {
        long pos;
        ret = fseek(fd, 0, SEEK_END);
        if (!ret) {
            ret = ftell(fd);
            if (!ret) {
                if (filelen < pos) {
#if __JVM_OS_VS__ || __JVM_OS_MINGW__ || __JVM_OS_CYGWIN__
                    fseek(fd, (long) filelen, SEEK_SET);
                    SetEndOfFile(fd);
#else
                    ret = ftruncate(fileno(fd), (off_t) filelen);
#endif
                } else {
                    u8 d = 0;
                    s32 i, imax = filelen - pos;
                    for (i = 0; i < imax; i++) {
                        fwrite(&d, 1, 1, fd);
                    }
                    fflush(fd);
                }
            }
        }
    }
    return ret;
}

s32 Java_org_mini_fs_InnerFile_write0__JI_I(JThreadRuntime *runtime, s64 p0, s32 p1) {
    FILE *fd = (FILE *) (intptr_t) p0;
    u8 byte = (u8) p1;
    if (fd) {
        s32 ret = fputc(byte, fd);
        if (ret == EOF) {
            return -1;
        } else {
            return p1;
        }
    }
    return -1;
}

s32 Java_org_mini_fs_InnerFile_writebuf__J_3BII_I(JThreadRuntime *runtime, s64 p0, JArray *p1, s32 p2, s32 p3) {
    FILE *fd = (FILE *) (intptr_t) p0;
    s32 offset = p2;
    s32 len = p3;
    s32 ret = -1;
    if (fd && p1 && (offset + len <= p1->prop.arr_length)) {
        ret = (s32) fwrite(p1->prop.as_s8_arr + offset, 1, len, fd);
        if (ret == 0) {
            ret = -1;
        }
    }
    return ret;
}

s32 Java_org_mini_net_SocketNative_accept0__I_I(JThreadRuntime *runtime, s32 p0) {
    s32 ret = 0;
    if (p0) {
        jthread_block_enter(runtime);
        ret = sock_accept(p0);
        jthread_block_exit(runtime);
    }
    return ret;
}

s32 Java_org_mini_net_SocketNative_available0__I_I(JThreadRuntime *runtime, s32 p0) {
    return 0;
}

s32 Java_org_mini_net_SocketNative_bind0__I_3BI_I(JThreadRuntime *runtime, s32 p0, JArray *p1, s32 p2) {
    s32 sockfd = p0;
    s32 port = p2;
    Utf8String *ip = utf8_create_part_c(p1->prop.as_s8_arr, 0, p1->prop.arr_length);

    jthread_block_enter(runtime);
    s32 ret = sock_bind(sockfd, ip, port);
    jthread_block_exit(runtime);
    utf8_destory(ip);
    return ret;
}

void Java_org_mini_net_SocketNative_close0__I_V(JThreadRuntime *runtime, s32 p0) {
    sock_close(p0);
}

s32 Java_org_mini_net_SocketNative_connect0__I_3BI_I(JThreadRuntime *runtime, s32 p0, JArray *p1, s32 p2) {
    s32 sockfd = p0;
    s32 port = p2;
    Utf8String *ip = utf8_create_part_c(p1->prop.as_s8_arr, 0, p1->prop.arr_length);

    jthread_block_enter(runtime);
    s32 ret = sock_connect(sockfd, ip, port);
    jthread_block_exit(runtime);
    utf8_destory(ip);
    return ret;
}

s32 Java_org_mini_net_SocketNative_getOption0__II_I(JThreadRuntime *runtime, s32 p0, s32 p1) {
    return sock_get_option(p0, p1);
}

struct java_lang_String *Java_org_mini_net_SocketNative_getSockAddr__II_Ljava_lang_String_2(JThreadRuntime *runtime, s32 p0, s32 p1) {
    s32 sockfd = p0;
    s32 mode = p1;
    if (sockfd) {
        struct sockaddr_in sock;
        socklen_t slen = sizeof(sock);
        if (mode == 0) {
            getpeername(sockfd, (struct sockaddr *) &sock, &slen);
        } else if (mode == 1) {
            getsockname(sockfd, (struct sockaddr *) &sock, &slen);
        }
#if __JVM_OS_MAC__ || __JVM_OS_LINUX__
#else
#endif
        char ipAddr[INET_ADDRSTRLEN];//保存点分十进制的地址
        Utf8String *ustr = utf8_create();
#if __JVM_OS_MINGW__ || __JVM_OS_CYGWIN__
        c8 *ipstr = inet_ntoa(sock.sin_addr);
        strcpy(ipAddr, ipstr);
#else
        inet_ntop(AF_INET, &sock.sin_addr, ipAddr, sizeof(ipAddr));
#endif
        int port = ntohs(sock.sin_port);
        utf8_append_c(ustr, ipAddr);
        utf8_append_c(ustr, ":");
        utf8_append_s64(ustr, port, 10);
        JObject *jstr = construct_string_with_cstr(runtime, utf8_cstr(ustr));
        utf8_destory(ustr);
        return (__refer) jstr;
    }
    return NULL;
}

s32 Java_org_mini_net_SocketNative_host2ip4___3B_I(JThreadRuntime *runtime, JArray *p0) {
    s32 addr = -1;
    if (p0) {
        Utf8String *ip = utf8_create_part_c(p0->prop.as_s8_arr, 0, p0->prop.arr_length);
        addr = host_2_ip4(ip);
        utf8_destory(ip);
    }
    return addr;
}

s32 Java_org_mini_net_SocketNative_listen0__I_I(JThreadRuntime *runtime, s32 p0) {
    if (p0) {
        jthread_block_enter(runtime);
        s32 ret = sock_listen(p0);
        jthread_block_exit(runtime);
        return ret;
    }
    return 0;
}

s32 Java_org_mini_net_SocketNative_open0___I(JThreadRuntime *runtime) {
    jthread_block_enter(runtime);
    s32 sockfd = sock_open();
    jthread_block_exit(runtime);
    return sockfd;
}

s32 Java_org_mini_net_SocketNative_readBuf__I_3BII_I(JThreadRuntime *runtime, s32 p0, JArray *p1, s32 p2, s32 p3) {
    s32 sockfd = p0;
    s32 offset = p2;
    s32 count = p3;

    jthread_block_enter(runtime);
    s32 len = sock_recv(sockfd, p1->prop.as_s8_arr + offset, count);
    jthread_block_exit(runtime);
    return len;
}

s32 Java_org_mini_net_SocketNative_readByte__I_I(JThreadRuntime *runtime, s32 p0) {
    s32 sockfd = p0;
    c8 b = 0;
    jthread_block_enter(runtime);
    s32 len = sock_recv(sockfd, &b, 1);
    jthread_block_exit(runtime);
    if (len < 0) {
        return len;
    }
    return b;
}

s32 Java_org_mini_net_SocketNative_setOption0__IIII_I(JThreadRuntime *runtime, s32 p0, s32 p1, s32 p2, s32 p3) {
    if (p0) {
        return sock_option(p0, p1, p2, p3);
    }
    return 0;
}


s32 Java_org_mini_net_SocketNative_sslc_1close___3B_I(JThreadRuntime *runtime, JArray *p0) {
    return sslc_close((SSLC_Entry *) p0->prop.as_c8_arr);
}


s32 Java_org_mini_net_SocketNative_sslc_1connect___3B_3B_3B_I(JThreadRuntime *runtime, JArray *p0, JArray *p1, JArray *p2) {
    return sslc_connect((SSLC_Entry *) p0->prop.as_c8_arr, p1->prop.as_c8_arr, p2->prop.as_c8_arr);
}


JArray *Java_org_mini_net_SocketNative_sslc_1construct_1entry____3B(JThreadRuntime *runtime) {
    int dimm = sizeof(SSLC_Entry);
    JArray *arr = multi_array_create_by_typename(runtime, &dimm, 1, "[B");
    return arr;
}


s32 Java_org_mini_net_SocketNative_sslc_1init___3B_I(JThreadRuntime *runtime, JArray *p0) {
    return sslc_init((SSLC_Entry *) p0->prop.as_c8_arr);
}


s32 Java_org_mini_net_SocketNative_sslc_1read___3B_3BII_I(JThreadRuntime *runtime, JArray *p0, JArray *p1, s32 p2, s32 p3) {
    return sslc_read((SSLC_Entry *) p0->prop.as_c8_arr, p1->prop.as_c8_arr + p2, p3);
}


s32 Java_org_mini_net_SocketNative_sslc_1write___3B_3BII_I(JThreadRuntime *runtime, JArray *p0, JArray *p1, s32 p2, s32 p3) {
    return sslc_write((SSLC_Entry *) p0->prop.as_c8_arr, p1->prop.as_c8_arr + p2, p3);
}


s32 Java_org_mini_net_SocketNative_writeBuf__I_3BII_I(JThreadRuntime *runtime, s32 p0, JArray *p1, s32 p2, s32 p3) {
    s32 sockfd = p0;
    s32 offset = p2;
    s32 count = p3;
    jthread_block_enter(runtime);
    s32 len = sock_send(sockfd, p1->prop.as_s8_arr + offset, count);
    jthread_block_exit(runtime);
    return len;
}

s32 Java_org_mini_net_SocketNative_writeByte__II_I(JThreadRuntime *runtime, s32 p0, s32 p1) {
    s32 sockfd = p0;
    c8 b = (u8) p1;
    jthread_block_enter(runtime);
    s32 len = sock_send(sockfd, &b, 1);
    jthread_block_exit(runtime);
    return len;
}

void Java_org_mini_reflect_DirectMemObj_copyFrom0__ILjava_lang_Object_2II_V(JThreadRuntime *runtime, struct org_mini_reflect_DirectMemObj *p0, s32 p1, struct java_lang_Object *p2, s32 p3, s32 p4) {
    s32 src_off = p1;
    s32 tgt_off = p3;
    s32 copy_len = p4;

    __refer memAddr = (__refer) (intptr_t) p0->memAddr_0;
    s32 dmo_len = p0->length_1;

    s32 ret = 0;
    if (src_off + copy_len > p2->prop.arr_length
        || tgt_off + copy_len > dmo_len) {
        JObject *exception = new_instance_with_name(runtime, STR_JAVA_LANG_ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION);
        instance_init(runtime, exception);
        throw_exception(runtime, exception);
    } else {
        s32 bytes = data_type_bytes[p2->prop.arr_type];
        memcpy((c8 *) memAddr + (bytes * tgt_off), (c8 *) p2->prop.as_s8_arr + (bytes * src_off), copy_len * (bytes));
    }
}

void Java_org_mini_reflect_DirectMemObj_copyTo0__ILjava_lang_Object_2II_V(JThreadRuntime *runtime, struct org_mini_reflect_DirectMemObj *p0, s32 p1, struct java_lang_Object *p2, s32 p3, s32 p4) {
    s32 src_off = p1;
    s32 tgt_off = p3;
    s32 copy_len = p4;

    __refer memAddr = (__refer) (intptr_t) p0->memAddr_0;
    s32 dmo_len = p0->length_1;


    s32 ret = 0;
    if (src_off + copy_len > dmo_len
        || tgt_off + copy_len > p2->prop.arr_length) {
        JObject *exception = new_instance_with_name(runtime, STR_JAVA_LANG_ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION);
        instance_init(runtime, exception);
        throw_exception(runtime, exception);
    } else {
        s32 bytes = data_type_bytes[p2->prop.arr_type];
        memcpy(p2->prop.as_s8_arr + (bytes * tgt_off), (c8 *) memAddr + (bytes * src_off), copy_len * (bytes));
    }
}

s64 Java_org_mini_reflect_DirectMemObj_getVal__I_J(JThreadRuntime *runtime, struct org_mini_reflect_DirectMemObj *p0, s32 p1) {
    s32 index = p1;

    __refer memAddr = (__refer) (intptr_t) p0->memAddr_0;
    s32 len = p0->length_1;
    c8 desc = p0->typeDesc_2;

    if (memAddr && index >= 0 && index < len) {
        s64 val;
        switch (desc) {
            case '1': {
                val = ((c8 *) (intptr_t) p0->memAddr_0)[index];
                break;
            }
            case '2': {
                val = ((s16 *) (intptr_t) p0->memAddr_0)[index];
                break;
            }
            case '4': {
                val = ((s32 *) (intptr_t) p0->memAddr_0)[index];
                break;
            }
            case '8': {
                val = ((s64 *) (intptr_t) p0->memAddr_0)[index];
                break;
            }
            case 'R': {
                val = (s64) (intptr_t) (((__refer *) (intptr_t) p0->memAddr_0)[index]);
                break;
            }
        }
        return val;
    } else {
        JObject *exception = new_instance_with_name(runtime, STR_JAVA_LANG_ILLEGAL_ARGUMENT_EXCEPTION);
        instance_init(runtime, exception);
        throw_exception(runtime, exception);
    }
    return 0;
}

void Java_org_mini_reflect_DirectMemObj_setVal__IJ_V(JThreadRuntime *runtime, struct org_mini_reflect_DirectMemObj *p0, s32 p1, s64 p2) {
    s32 index = p1;

    __refer memAddr = (__refer) (intptr_t) p0->memAddr_0;
    s32 len = p0->length_1;
    c8 desc = p0->typeDesc_2;

    if (memAddr && index >= 0 && index < len) {
        switch (desc) {
            case '1': {
                ((c8 *) (intptr_t) p0->memAddr_0)[index] = (s8) p2;
                break;
            }
            case '2': {
                ((s16 *) (intptr_t) p0->memAddr_0)[index] = (s16) p2;
                break;
            }
            case '4': {
                ((s32 *) (intptr_t) p0->memAddr_0)[index] = (s32) p2;
                break;
            }
            case '8': {
                ((s64 *) (intptr_t) p0->memAddr_0)[index] = (s64) p2;
                break;
            }
            case 'R': {
                ((__refer *) (intptr_t) p0->memAddr_0)[index] = (__refer) (intptr_t) p2;
                break;
            }
        }
    } else {
        JObject *exception = new_instance_with_name(runtime, STR_JAVA_LANG_ILLEGAL_ARGUMENT_EXCEPTION);
        instance_init(runtime, exception);
        throw_exception(runtime, exception);
    }
}

s64 Java_org_mini_reflect_ReflectArray_getBodyPtr__Ljava_lang_Object_2_J(JThreadRuntime *runtime, struct java_lang_Object *p0) {
    InstProp *prop = (InstProp *) p0;
    if (prop && prop->type == INS_TYPE_ARRAY) {
        return (s64) (intptr_t) prop->as_s8_arr;
    }
    return 0;
}

s32 Java_org_mini_reflect_ReflectArray_getLength__Ljava_lang_Object_2_I(JThreadRuntime *runtime, struct java_lang_Object *p0) {
    InstProp *prop = (InstProp *) p0;
    if (prop && prop->type == INS_TYPE_ARRAY) {
        return prop->arr_length;
    }
    return 0;
}

s8 Java_org_mini_reflect_ReflectArray_getTypeTag__Ljava_lang_Object_2_B(JThreadRuntime *runtime, struct java_lang_Object *p0) {
    InstProp *prop = (InstProp *) p0;
    if (prop && prop->type == INS_TYPE_ARRAY) {
        return utf8_char_at(prop->clazz->name, 1);
    }
    return 0;
}

struct java_lang_Object *Java_org_mini_reflect_ReflectArray_multiNewArray__Ljava_lang_Class_2_3I_Ljava_lang_Object_2(JThreadRuntime *runtime, struct java_lang_Class *p0, JArray *p1) {
    JClass *cl = (__refer) (intptr_t) p0->classHandle_in_class;
    Utf8String *desc = utf8_create();
    if (cl->primitive) {
        utf8_pushback(desc, getDataTypeTagByName(cl->name));
    } else if (cl->prop.arr_type) {
        utf8_append(desc, cl->name);
    } else {
        utf8_append_c(desc, "L");
        utf8_append(desc, cl->name);
        utf8_append_c(desc, ";");
    }
    s32 i;
    for (i = 0; i < p1->prop.arr_length; i++) {
        utf8_insert(desc, 0, '[');
    }

    JArray *arr = multi_array_create_by_typename(runtime, p1->prop.as_s32_arr, p1->prop.arr_length, utf8_cstr(desc));
    utf8_destory(desc);
    return (__refer) arr;
}

struct java_lang_Object *Java_org_mini_reflect_ReflectArray_newArray__Ljava_lang_Class_2I_Ljava_lang_Object_2(JThreadRuntime *runtime, struct java_lang_Class *p0, s32 p1) {
    JClass *cl = (__refer) (intptr_t) p0->classHandle_in_class;
    Utf8String *desc = utf8_create_c("[");
    if (cl->primitive) {
        utf8_pushback(desc, getDataTypeTagByName(cl->name));
    } else if (cl->prop.arr_type) {
        utf8_append(desc, cl->name);
    } else {
        utf8_append_c(desc, "L");
        utf8_append(desc, cl->name);
        utf8_append_c(desc, ";");
    }

    JArray *arr = multi_array_create_by_typename(runtime, &p1, 1, utf8_cstr(desc));
    utf8_destory(desc);
    return (__refer) arr;
}

void Java_org_mini_reflect_ReflectClass_mapReference__J_V(JThreadRuntime *runtime, struct org_mini_reflect_ReflectClass *p0, s64 p1) {
    if (!p0)return;
    JClass *target = (__refer) (intptr_t) p1;
    p0->className_5 = (__refer) construct_string_with_ustr(runtime, target->name);
    p0->superclass_4 = (s64) (intptr_t) (getSuperClass(target) ? construct_string_with_ustr(runtime, getSuperClass(target)->name) : NULL);
    p0->accessFlags_6 = target->raw->acc_flag;
    p0->status_9 = target->status;
    p0->source_7 = (__refer) construct_string_with_ustr(runtime, target->source_name);
    p0->signature_8 = NULL;
    s32 i;
    {
        JArray *jarr = multi_array_create_by_typename(runtime, &target->fields->length, 1, "[J");
        p0->fieldIds_10 = jarr;
        for (i = 0; i < target->fields->length; i++) {
            jarr->prop.as_s64_arr[i] = (s64) (intptr_t) arraylist_get_value(target->fields, i);
        }
    }
    //
    {
        JArray *jarr = multi_array_create_by_typename(runtime, &target->methods->length, 1, "[J");
        p0->methodIds_11 = jarr;
        for (i = 0; i < target->methods->length; i++) {
            jarr->prop.as_s64_arr[i] = (u64) (intptr_t) arraylist_get_value(target->methods, i);
        }
    }
    //
    {
        JArray *jarr = multi_array_create_by_typename(runtime, &target->interfaces->length, 1, "[J");
        p0->interfaces_12 = jarr;
        for (i = 0; i < target->interfaces->length; i++) {
            jarr->prop.as_s64_arr[i] = (u64) (intptr_t) arraylist_get_value(target->interfaces, i);
        }
    }
}

s64 Java_org_mini_reflect_ReflectField_getFieldVal__Ljava_lang_Object_2J_J(JThreadRuntime *runtime, struct java_lang_Object *p0, s64 p1) {
    FieldInfo *fieldInfo = (__refer) (intptr_t) p1;
    s64 v = 0;
    if (p1) {
        c8 tag = utf8_char_at(fieldInfo->desc, 0);
        switch (tag) {
            case 'B': {
                v = *(s8 *) ((c8 *) p0 + fieldInfo->offset_ins);
                break;
            }
            case 'C': {
                v = *(u16 *) ((c8 *) p0 + fieldInfo->offset_ins);
                break;
            }
            case 'S': {
                v = *(s16 *) ((c8 *) p0 + fieldInfo->offset_ins);
                break;
            }
            case 'Z': {
                v = *(s32 *) ((c8 *) p0 + fieldInfo->offset_ins);
                break;
            }
            case 'I': {
                v = *(s32 *) ((c8 *) p0 + fieldInfo->offset_ins);
                break;
            }
            case 'J': {
                v = *(s64 *) ((c8 *) p0 + fieldInfo->offset_ins);
                break;
            }
            case 'F': {
                v = *(s32 *) ((c8 *) p0 + fieldInfo->offset_ins);
                break;
            }
            case 'D': {
                v = *(s64 *) ((c8 *) p0 + fieldInfo->offset_ins);
                break;
            }
            case '[':
            case 'L': {
                __refer ref = *(__refer *) ((c8 *) p0 + fieldInfo->offset_ins);
                v = (s64) (intptr_t) ref;
                break;
            }
        }
    }

    return v;
}

void Java_org_mini_reflect_ReflectField_mapField__J_V(JThreadRuntime *runtime, struct org_mini_reflect_ReflectField *p0, s64 p1) {
    FieldInfo *fieldInfo = (__refer) (intptr_t) p1;
    if (p1) {
        p0->fieldName_2 = (__refer) construct_string_with_ustr(runtime, fieldInfo->name);
        p0->descriptor_3 = (__refer) construct_string_with_ustr(runtime, fieldInfo->desc);
        p0->signature_4 = NULL;
        p0->accessFlags_5 = fieldInfo->raw->access;
        p0->type_6 = utf8_char_at(fieldInfo->desc, 0);
    }
}

void Java_org_mini_reflect_ReflectField_setFieldVal__Ljava_lang_Object_2JJ_V(JThreadRuntime *runtime, struct java_lang_Object *p0, s64 p1, s64 p2) {
    FieldInfo *fieldInfo = (__refer) (intptr_t) p1;
    if (p1) {
        c8 tag = utf8_char_at(fieldInfo->desc, 0);
        switch (tag) {
            case 'B': {
                *(s8 *) ((c8 *) p0 + fieldInfo->offset_ins) = (s8) p2;
                break;
            }
            case 'C': {
                *(u16 *) ((c8 *) p0 + fieldInfo->offset_ins) = (u16) p2;
                break;
            }
            case 'S': {
                *(s16 *) ((c8 *) p0 + fieldInfo->offset_ins) = (s16) p2;
                break;
            }
            case 'Z': {
                *(s32 *) ((c8 *) p0 + fieldInfo->offset_ins) = (s32) p2;
                break;
            }
            case 'I': {
                *(s32 *) ((c8 *) p0 + fieldInfo->offset_ins) = (s32) p2;
                break;
            }
            case 'J': {
                *(s64 *) ((c8 *) p0 + fieldInfo->offset_ins) = (s64) p2;
                break;
            }
            case 'F': {
                *(f32 *) ((c8 *) p0 + fieldInfo->offset_ins) = *(f32 *) &p2;
                break;
            }
            case 'D': {
                *(f64 *) ((c8 *) p0 + fieldInfo->offset_ins) = *(f64 *) &p2;
                break;
            }
            case '[':
            case 'L': {
                *(__refer *) ((c8 *) p0 + fieldInfo->offset_ins) = (__refer) (intptr_t) p2;
                break;
            }
        }
    }
}

s64 Java_org_mini_reflect_ReflectMethod_findMethod0__Ljava_lang_String_2Ljava_lang_String_2Ljava_lang_String_2_J(JThreadRuntime *runtime, struct java_lang_String *p0, struct java_lang_String *p1, struct java_lang_String *p2) {
    return 0;
}

struct org_mini_reflect_DataWrap *Java_org_mini_reflect_ReflectMethod_invokeMethod__JLjava_lang_Object_2_3J_Lorg_mini_reflect_DataWrap_2(JThreadRuntime *runtime, struct org_mini_reflect_ReflectMethod *p0, s64 p1, struct java_lang_Object *p2, JArray *p3) {
    MethodInfo *method = (__refer) (intptr_t)
            p1;
    struct org_mini_reflect_DataWrap *result = (struct org_mini_reflect_DataWrap *) new_instance_with_name(runtime, "org/mini/reflect/DataWrap");
    gc_refer_hold(result);
    instance_init(runtime, (JObject *) result);
    s32 isVoid = utf8_char_at(method->returntype, 0) == 'V';
    if (p3->prop.arr_length == 0) {
        switch (utf8_char_at(method->returntype, 0)) {
            case 'B': {
                s8 (*func_ptr)(JThreadRuntime *) =method->raw->func_ptr;
                result->nv_1 = func_ptr(runtime);
                break;
            }
            case 'C': {
                u16 (*func_ptr)(JThreadRuntime *) =method->raw->func_ptr;
                result->nv_1 = func_ptr(runtime);
                break;
            }
            case 'S': {
                s16 (*func_ptr)(JThreadRuntime *) =method->raw->func_ptr;
                result->nv_1 = func_ptr(runtime);
                break;
            }
            case 'Z': {
                s32 (*func_ptr)(JThreadRuntime *) =method->raw->func_ptr;
                result->nv_1 = func_ptr(runtime);
                break;
            }
            case 'I': {
                s32 (*func_ptr)(JThreadRuntime *) =method->raw->func_ptr;
                result->nv_1 = func_ptr(runtime);
                break;
            }
            case 'J': {
                s64 (*func_ptr)(JThreadRuntime *) =method->raw->func_ptr;
                result->nv_1 = func_ptr(runtime);
                break;
            }
            case 'F': {
                f32 (*func_ptr)(JThreadRuntime *) =method->raw->func_ptr;
                f32 v = func_ptr(runtime);
                result->nv_1 = *(s32 *) &v;
                break;
            }
            case 'D': {
                f64 (*func_ptr)(JThreadRuntime *) =method->raw->func_ptr;
                f64 v = func_ptr(runtime);
                result->nv_1 = *(s64 *) &v;
                break;
            }
            case '[':
            case 'L': {
                __refer (*func_ptr)(JThreadRuntime *) =method->raw->func_ptr;
                result->ov_0 = (__refer) func_ptr(runtime);
                break;
            }
        }
    } else if (p3->prop.arr_length == 1) {
        s64 v1 = p3->prop.as_s64_arr[0];

        if ((method->raw->access & ACC_STATIC) == 0) {//
            if (isVoid) {
                switch (utf8_char_at(method->paratype, 0)) {
                    case 'B': {
                        void (*func_ptr)(JThreadRuntime *, __refer, s8) =method->raw->func_ptr;
                        func_ptr(runtime, p2, (s8) v1);
                        break;
                    }
                    case 'C': {
                        void (*func_ptr)(JThreadRuntime *, __refer, u16) =method->raw->func_ptr;
                        func_ptr(runtime, p2, (u16) v1);
                        break;
                    }
                    case 'S': {
                        void (*func_ptr)(JThreadRuntime *, __refer, s16) =method->raw->func_ptr;
                        func_ptr(runtime, p2, (s16) v1);
                        break;
                    }
                    case 'Z': {
                        void (*func_ptr)(JThreadRuntime *, __refer, s32) =method->raw->func_ptr;
                        func_ptr(runtime, p2, (s32) v1);
                        break;
                    }
                    case 'I': {
                        void (*func_ptr)(JThreadRuntime *, __refer, s32) =method->raw->func_ptr;
                        func_ptr(runtime, p2, (s32) v1);
                        break;
                    }
                    case 'J': {
                        void (*func_ptr)(JThreadRuntime *, __refer, s64) =method->raw->func_ptr;
                        func_ptr(runtime, p2, (s64) v1);
                        break;
                    }
                    case 'F': {
                        void (*func_ptr)(JThreadRuntime *, __refer, f32) =method->raw->func_ptr;
                        func_ptr(runtime, p2, *(f32 *) &v1);
                        break;
                    }
                    case 'D': {
                        void (*func_ptr)(JThreadRuntime *, __refer, f64) =method->raw->func_ptr;
                        func_ptr(runtime, p2, *(f64 *) &v1);
                        break;
                    }
                    case '[':
                    case 'L': {
                        void (*func_ptr)(JThreadRuntime *, __refer, __refer) =method->raw->func_ptr;
                        func_ptr(runtime, p2, (__refer) (intptr_t) v1);
                        break;
                    }
                }
            } else {
                jvm_printf("invoke not support non-static with return value\n");
            }
        } else {
            jvm_printf("invoke not support static \n");
        }
    } else {
        jvm_printf("invoke not support para more than 1 \n");
    }
    gc_refer_release(result);
    return result;
}

void Java_org_mini_reflect_ReflectMethod_mapMethod__J_V(JThreadRuntime *runtime, struct org_mini_reflect_ReflectMethod *p0, s64 p1) {
    MethodInfo *methodInfo = (__refer) (intptr_t) p1;
    if (p1) {
        p0->methodName_2 = (__refer) construct_string_with_ustr(runtime, methodInfo->name);
        p0->descriptor_3 = (__refer) construct_string_with_ustr(runtime, methodInfo->desc);
        p0->signature_4 = (__refer) construct_string_with_ustr(runtime, methodInfo->signature);;
        p0->accessFlags_5 = methodInfo->raw->access;
        p0->argCnt_10 = 0;
        p0->codeStart_6 = 0;
        p0->codeEnd_7 = 0;
        p0->lines_8 = 0;
        p0->lineNum_9 = NULL;
        p0->localVarTable_11 = NULL;
    }
}

void Java_org_mini_reflect_StackFrame_mapRuntime__J_V(JThreadRuntime *runtime, struct org_mini_reflect_StackFrame *p0, s64 p1) {
    return;
}

void Java_org_mini_reflect_vm_RefNative_addJarToClasspath__Ljava_lang_String_2_V(JThreadRuntime *runtime, struct java_lang_String *p0) {
    return;
}

struct java_lang_Class *Java_org_mini_reflect_vm_RefNative_defineClass__Ljava_lang_ClassLoader_2Ljava_lang_String_2_3BII_Ljava_lang_Class_2(JThreadRuntime *runtime, struct java_lang_ClassLoader *p0, struct java_lang_String *p1, JArray *p2, s32 p3, s32 p4) {
    Utf8String *ustr = utf8_create();
    jstring_2_utf8(p1, ustr);
    utf8_replace_c(ustr, ".", "/");
    JClass *cl = get_class_by_name(ustr);
    utf8_destory(ustr);
    if (cl && (cl->jclass_loader == (JObject *) p0)) {
        return (java_lang_Class *) cl->ins_of_Class;
    }
    jvm_printf("java2c can't define Class by class data.");
    return NULL;//
}

struct java_lang_Class *Java_org_mini_reflect_vm_RefNative_findLoadedClass0__Ljava_lang_ClassLoader_2Ljava_lang_String_2_Ljava_lang_Class_2(JThreadRuntime *runtime, struct java_lang_ClassLoader *p0, struct java_lang_String *p1) {
    Utf8String *ustr = utf8_create();
    jstring_2_utf8(p1, ustr);
    utf8_replace_c(ustr, ".", "/");
    JClass *cl = get_class_by_name(ustr);
    utf8_destory(ustr);
    if (cl && (cl->jclass_loader == (JObject *) p0)) {
        return (java_lang_Class *) cl->ins_of_Class;
    }
    return NULL;
}

struct java_lang_Class *Java_org_mini_reflect_vm_RefNative_getCallerClass___Ljava_lang_Class_2(JThreadRuntime *runtime) {
    StackFrame *tail = runtime->tail;
    if (tail->next) {
        if (tail->next->next) {
            return (java_lang_Class *) get_methodinfo_by_rawindex(tail->next->next->methodRawIndex)->clazz->ins_of_Class;
        }
    }

    return NULL;
}

struct java_lang_Class *Java_org_mini_reflect_vm_RefNative_getClassByName__Ljava_lang_String_2_Ljava_lang_Class_2(JThreadRuntime *runtime, struct java_lang_String *p0) {
    Utf8String *ustr = utf8_create();
    jstring_2_utf8(p0, ustr);
    utf8_replace_c(ustr, ".", "/");
    class_clinit(runtime, ustr);
    JClass *cl = get_class_by_name(ustr);
    utf8_destory(ustr);
    return (__refer) ins_of_Class_create_get(runtime, cl);
}

JArray *Java_org_mini_reflect_vm_RefNative_getClasses____3Ljava_lang_Class_2(JThreadRuntime *runtime) {
    s32 size = (s32) g_jvm->classes->entries;

    JArray *jarr = multi_array_create_by_typename(runtime, &size, 1, STR_JAVA_LANG_CLASS);
    s32 i = 0;
    HashtableIterator hti;
    hashtable_iterate(g_jvm->classes, &hti);

    for (; hashtable_iter_has_more(&hti);) {
        Utf8String *k = hashtable_iter_next_key(&hti);
        JClass *r = get_class_by_name(k);
        jarr->prop.as_obj_arr[i] = ins_of_Class_create_get(runtime, r);
        i++;
    }
    return jarr;
}

s32 Java_org_mini_reflect_vm_RefNative_getFrameCount__Ljava_lang_Thread_2_I(JThreadRuntime *runtime, struct java_lang_Thread *p0) {
    return 0;
}

JArray *Java_org_mini_reflect_vm_RefNative_getGarbageReferedObjs____3Ljava_lang_Object_2(JThreadRuntime *runtime) {
    return NULL;
}

s32 Java_org_mini_reflect_vm_RefNative_getGarbageStatus___I(JThreadRuntime *runtime) {
    return g_jvm->collector->_garbage_thread_status;
}

s32 Java_org_mini_reflect_vm_RefNative_getLocalVal__JILorg_mini_reflect_vm_ValueType_2_I(JThreadRuntime *runtime, s64 p0, s32 p1, struct org_mini_reflect_vm_ValueType *p2) {
    return 0;
}

s64 Java_org_mini_reflect_vm_RefNative_getStackFrame__Ljava_lang_Thread_2_J(JThreadRuntime *runtime, struct java_lang_Thread *p0) {
    return 0;
}

s32 Java_org_mini_reflect_vm_RefNative_getStatus__Ljava_lang_Thread_2_I(JThreadRuntime *runtime, struct java_lang_Thread *p0) {
    return 0;
}

s32 Java_org_mini_reflect_vm_RefNative_getSuspendCount__Ljava_lang_Thread_2_I(JThreadRuntime *runtime, struct java_lang_Thread *p0) {
    return 0;
}

JArray *Java_org_mini_reflect_vm_RefNative_getThreads____3Ljava_lang_Thread_2(JThreadRuntime *runtime) {
    JArray *jarr = multi_array_create_by_typename(runtime, &g_jvm->thread_list->length, 1, STR_JAVA_LANG_THREAD);

    spin_lock(&g_jvm->thread_list->spinlock);
    s32 i = 0;
    for (i = 0; i < g_jvm->thread_list->length; i++) {
        JThreadRuntime *r = arraylist_get_value(g_jvm->thread_list, i);
        if (r) {
            jarr->prop.as_obj_arr[i] = r->jthread;
        }
    }
    spin_unlock(&g_jvm->thread_list->spinlock);
    return jarr;
}

s64 Java_org_mini_reflect_vm_RefNative_heap_1calloc__I_J(JThreadRuntime *runtime, s32 p0) {
    return (s64) (intptr_t) jvm_calloc(p0);
}

void Java_org_mini_reflect_vm_RefNative_heap_1copy__JIJII_V(JThreadRuntime *runtime, s64 p0, s32 p1, s64 p2, s32 p3, s32 p4) {
    memcpy((s8 *) (intptr_t) p2 + p3, (s8 *) (intptr_t) p0 + p1, p4);
}

s32 Java_org_mini_reflect_vm_RefNative_heap_1endian___I(JThreadRuntime *runtime) {
    return 1;
}

void Java_org_mini_reflect_vm_RefNative_heap_1free__J_V(JThreadRuntime *runtime, s64 p0) {
    jvm_free((__refer) (intptr_t) p0);
}

s8 Java_org_mini_reflect_vm_RefNative_heap_1get_1byte__JI_B(JThreadRuntime *runtime, s64 p0, s32 p1) {
    return *(s8 *) ((c8 *) (intptr_t) p0 + p1);
}

f64 Java_org_mini_reflect_vm_RefNative_heap_1get_1double__JI_D(JThreadRuntime *runtime, s64 p0, s32 p1) {
    return *(f64 *) ((c8 *) (intptr_t) p0 + p1);
}

f32 Java_org_mini_reflect_vm_RefNative_heap_1get_1float__JI_F(JThreadRuntime *runtime, s64 p0, s32 p1) {
    return *(f32 *) ((c8 *) (intptr_t) p0 + p1);
}

s32 Java_org_mini_reflect_vm_RefNative_heap_1get_1int__JI_I(JThreadRuntime *runtime, s64 p0, s32 p1) {
    return *(s32 *) ((c8 *) (intptr_t) p0 + p1);
}

s64 Java_org_mini_reflect_vm_RefNative_heap_1get_1long__JI_J(JThreadRuntime *runtime, s64 p0, s32 p1) {
    return *(s64 *) ((c8 *) (intptr_t) p0 + p1);
}

struct java_lang_Object *Java_org_mini_reflect_vm_RefNative_heap_1get_1ref__JI_Ljava_lang_Object_2(JThreadRuntime *runtime, s64 p0, s32 p1) {
    return *(__refer *) ((c8 *) (intptr_t) p0 + p1);
}

s16 Java_org_mini_reflect_vm_RefNative_heap_1get_1short__JI_S(JThreadRuntime *runtime, s64 p0, s32 p1) {
    return *(s16 *) ((c8 *) (intptr_t) p0 + p1);
}

void Java_org_mini_reflect_vm_RefNative_heap_1put_1byte__JIB_V(JThreadRuntime *runtime, s64 p0, s32 p1, s8 p2) {
    *(s8 *) ((c8 *) (intptr_t) p0 + p1) = p2;
}

void Java_org_mini_reflect_vm_RefNative_heap_1put_1double__JID_V(JThreadRuntime *runtime, s64 p0, s32 p1, f64 p2) {
    *(f64 *) ((c8 *) (intptr_t) p0 + p1) = p2;
}

void Java_org_mini_reflect_vm_RefNative_heap_1put_1float__JIF_V(JThreadRuntime *runtime, s64 p0, s32 p1, f32 p2) {
    *(f32 *) ((c8 *) (intptr_t) p0 + p1) = p2;
}

void Java_org_mini_reflect_vm_RefNative_heap_1put_1int__JII_V(JThreadRuntime *runtime, s64 p0, s32 p1, s32 p2) {
    *(s32 *) ((c8 *) (intptr_t) p0 + p1) = p2;
}

void Java_org_mini_reflect_vm_RefNative_heap_1put_1long__JIJ_V(JThreadRuntime *runtime, s64 p0, s32 p1, s64 p2) {
    *(s64 *) ((c8 *) (intptr_t) p0 + p1) = p2;
}

void Java_org_mini_reflect_vm_RefNative_heap_1put_1ref__JILjava_lang_Object_2_V(JThreadRuntime *runtime, s64 p0, s32 p1, struct java_lang_Object *p2) {
    *(__refer *) ((c8 *) (intptr_t) p0 + p1) = p2;
}

void Java_org_mini_reflect_vm_RefNative_heap_1put_1short__JIS_V(JThreadRuntime *runtime, s64 p0, s32 p1, s16 p2) {
    *(s16 *) ((c8 *) (intptr_t) p0 + p1) = p2;
}

struct java_lang_Object *Java_org_mini_reflect_vm_RefNative_id2obj__J_Ljava_lang_Object_2(JThreadRuntime *runtime, s64 p0) {
    return (__refer) (intptr_t) p0;
}

struct java_lang_Object *Java_org_mini_reflect_vm_RefNative_newWithoutInit__Ljava_lang_Class_2_Ljava_lang_Object_2(JThreadRuntime *runtime, struct java_lang_Class *p0) {
    JClass *cl = (__refer) (intptr_t) p0->classHandle_in_class;
    JObject *ins = NULL;
    if (cl && !cl->prop.arr_type) {//class exists and not array class
        ins = new_instance_with_class(runtime, cl);
    }
    if (!ins) {
        JObject *exception = new_instance_with_name(runtime, STR_JAVA_LANG_INSTANTIATION_EXCEPTION);
        instance_init(runtime, exception);
        throw_exception(runtime, exception);
    }
    return (__refer) ins;
}

s64 Java_org_mini_reflect_vm_RefNative_obj2id__Ljava_lang_Object_2_J(JThreadRuntime *runtime, struct java_lang_Object *p0) {
    return (s64) (intptr_t) p0;
}

s32 Java_org_mini_reflect_vm_RefNative_refIdSize___I(JThreadRuntime *runtime) {
    return sizeof(__refer);
}

s32 Java_org_mini_reflect_vm_RefNative_resumeThread__Ljava_lang_Thread_2_I(JThreadRuntime *runtime, struct java_lang_Thread *p0) {
    return 0;
}

s32 Java_org_mini_reflect_vm_RefNative_setLocalVal__JIBJI_I(JThreadRuntime *runtime, s64 p0, s32 p1, s8 p2, s64 p3, s32 p4) {
    return 0;
}

s32 Java_org_mini_reflect_vm_RefNative_stopThread__Ljava_lang_Thread_2J_I(JThreadRuntime *runtime, struct java_lang_Thread *p0, s64 p1) {
    return 0;
}

s32 Java_org_mini_reflect_vm_RefNative_suspendThread__Ljava_lang_Thread_2_I(JThreadRuntime *runtime, struct java_lang_Thread *p0) {
    return 0;
}

s32 Java_org_mini_urlhandler_ResourceHandler_00024ResourceInputStream_available__JI_I(JThreadRuntime *runtime, s64 p0, s32 p1) {
    return 0;
}

void Java_org_mini_urlhandler_ResourceHandler_00024ResourceInputStream_close__J_V(JThreadRuntime *runtime, s64 p0) {
    return;
}

s32 Java_org_mini_urlhandler_ResourceHandler_00024ResourceInputStream_getContentLength__Ljava_lang_String_2_I(JThreadRuntime *runtime, struct java_lang_String *p0) {
    return 0;
}

s64 Java_org_mini_urlhandler_ResourceHandler_00024ResourceInputStream_open__Ljava_lang_String_2_J(JThreadRuntime *runtime, struct java_lang_String *p0) {
    return 0;
}

s32 Java_org_mini_urlhandler_ResourceHandler_00024ResourceInputStream_read__JI_I(JThreadRuntime *runtime, s64 p0, s32 p1) {
    return 0;
}

s32 Java_org_mini_urlhandler_ResourceHandler_00024ResourceInputStream_read__JI_3BII_I(JThreadRuntime *runtime, s64 p0, s32 p1, JArray *p2, s32 p3, s32 p4) {
    return 0;
}

JArray *Java_org_mini_zip_Zip_compress0___3B__3B(JThreadRuntime *runtime, JArray *p0) {
    s32 ret = 0;
    ByteBuf *zip_data = bytebuf_create(0);
    JArray *jarr = NULL;
    if (p0) {
        ret = zip_compress(p0->prop.as_s8_arr, p0->prop.arr_length, zip_data);
    }
    if (ret == -1) {
    } else {
        jarr = multi_array_create_by_typename(runtime, (s32 *) &zip_data->wp, 1, "[B");
        bytebuf_read_batch(zip_data, jarr->prop.as_s8_arr, zip_data->wp);
    }
    bytebuf_destory(zip_data);
    return jarr;
}

JArray *Java_org_mini_zip_Zip_extract0___3B__3B(JThreadRuntime *runtime, JArray *p0) {
    s32 ret = 0;
    ByteBuf *data = bytebuf_create(0);
    JArray *jarr = NULL;
    if (p0) {
        ret = zip_extract(p0->prop.as_s8_arr, p0->prop.arr_length, data);
    }
    if (ret == -1) {
    } else {
        jarr = multi_array_create_by_typename(runtime, (s32 *) &data->wp, 1, "[B");
        bytebuf_read_batch(data, jarr->prop.as_s8_arr, data->wp);
    }
    bytebuf_destory(data);
    return NULL;
}

s32 Java_org_mini_zip_Zip_fileCount0___3B_I(JThreadRuntime *runtime, JArray *p0) {
    return zip_filecount(p0->prop.as_s8_arr);
}

JArray *Java_org_mini_zip_Zip_getEntry0___3B_3B__3B(JThreadRuntime *runtime, JArray *p0, JArray *p1) {
    JArray *zip_path_arr = p0;
    JArray *name_arr = p1;
    JArray *jarr = NULL;
    if (zip_path_arr && name_arr) {
        ByteBuf *buf = bytebuf_create(0);
        zip_loadfile(zip_path_arr->prop.as_s8_arr, name_arr->prop.as_s8_arr, buf);
        if (buf->wp) {
            jarr = multi_array_create_by_typename(runtime, (s32 *) &buf->wp, 1, "[B");
            memmove(jarr->prop.as_s8_arr, buf->buf, buf->wp);
        }
        bytebuf_destory(buf);
    }
    return jarr;
}

s32 Java_org_mini_zip_Zip_isDirectory0___3BI_I(JThreadRuntime *runtime, JArray *p0, s32 p1) {
    return zip_is_directory(p0->prop.as_s8_arr, p1);
}

JArray *Java_org_mini_zip_Zip_listFiles0___3B__3Ljava_lang_String_2(JThreadRuntime *runtime, JArray *p0) {
    JArray *zip_path_arr = p0;
    JArray *jarr = NULL;
    if (zip_path_arr) {
        ArrayList *list = zip_get_filenames(zip_path_arr->prop.as_s8_arr);
        if (list) {
            jarr = multi_array_create_by_typename(runtime, &list->length, 1, "[Ljava/lang/String;");
            instance_hold_to_thread(runtime, jarr);
            s32 i;
            for (i = 0; i < list->length; i++) {
                Utf8String *ustr = arraylist_get_value_unsafe(list, i);
                JObject *jstr = construct_string_with_ustr(runtime, ustr);
                jarr->prop.as_obj_arr[i] = jstr;
            }
            zip_destory_filenames_list(list);
            instance_release_from_thread(runtime, jarr);
        }
    }
    return jarr;
}

s32 Java_org_mini_zip_Zip_putEntry0___3B_3B_3B_I(JThreadRuntime *runtime, JArray *p0, JArray *p1, JArray *p2) {
    JArray *zip_path_arr = p0;
    JArray *name_arr = p1;
    JArray *content_arr = p2;
    s32 ret = -1;
    if (zip_path_arr && name_arr && content_arr) {
        zip_savefile_mem(zip_path_arr->prop.as_s8_arr, name_arr->prop.as_s8_arr, content_arr->prop.as_s8_arr, content_arr->prop.arr_length);
        ret = 0;
    }
    return ret;
}







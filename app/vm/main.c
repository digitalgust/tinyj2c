#include <errno.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>


#include "jvm.h"
#include "bytebuf.h"

void _on_jvm_sig(int no) {

    printf("[ERROR]tinyj2c signo:%d  errno: %d , %s\n", no, errno, strerror(errno));
    exit(no);
}

s32 main(int argc, const char *argv[]) {
#ifdef SIGABRT
    signal(SIGABRT, _on_jvm_sig);
#endif
#ifdef SIGFPE
    signal(SIGFPE, _on_jvm_sig);
#endif
#ifdef SIGSEGV
    signal(SIGSEGV, _on_jvm_sig);
#endif
#ifdef SIGTERM
    signal(SIGTERM, _on_jvm_sig);
#endif
#ifdef SIGPIPE
    signal(SIGPIPE, _on_jvm_sig);
#endif

    Utf8String *mainClassName = utf8_create();
    if (argc > 1) {
        utf8_append_c(mainClassName, (c8 *) argv[1]);
    } else {
        utf8_clear(mainClassName);
//        utf8_append_c(mainClassName, "test.SpecTest");
//        utf8_append_c(mainClassName, "test.BpDeepTest");
//        utf8_append_c(mainClassName, "test.Foo1");
        utf8_append_c(mainClassName, "test.HelloWorld");
//        utf8_append_c(mainClassName, "org.luaj.vm2.lib.jme.TestLuaJ");
        jvm_printf("[INFO]ccjvm test.HelloWorld\n");
    }
    s32 ret = jvm_run_main(mainClassName);
    utf8_destory(mainClassName);
	//getchar();
    return ret;

}

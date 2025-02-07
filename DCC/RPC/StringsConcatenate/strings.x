%{
#include <rpc/rpc.h>
%}
program TEST_PROG {
    version TEST_VERS {
        int TEST_FUNC(int) = 1;
    } = 1;
} = 0x20000001;

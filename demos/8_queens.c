#include <stdio.h>

extern void GLSL_solve(int *, char *);
extern int ans[8];

int main() {
    int depth = 0;
    char res;
    GLSL_solve(&depth, &res);
    puts(res ? "yes" : "no");
    for (int i = 0; i < 8; i++) printf("%d, ", ans[i]);
    puts("");
    return 0;
}

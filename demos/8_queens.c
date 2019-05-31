#include <stdio.h>

extern void solve(int *, char *);
extern int ans[8];

int main() {
    int depth = 0;
    char res;
    solve(&depth, &res);
    puts(res ? "yes" : "no");
    for (int i = 0; i < 8; i++) printf("%d, ", ans[i]);
    puts("");
    return 0;
}

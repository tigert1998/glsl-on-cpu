#include <cmath>
#include <cstdio>
#include <algorithm>

extern "C"
{
    void GLSL_cos(float *x, float *ret);
    void GLSL_sin(float *x, float *ret);
    void GLSL_floor(float *x, float *ret);
    void GLSL_pow(float *x, float *y, float *ret);
    void GLSL_mainImage(float *(*fragColor)[4], float *(*fragCoord)[2]);
}

extern float iResolution[3];

void GLSL_cos(float *x, float *ret)
{
    *ret = std::cos(*x);
}

void GLSL_sin(float *x, float *ret)
{
    *ret = std::sin(*x);
}

void GLSL_floor(float *x, float *ret)
{
    *ret = std::floor(*x);
}

void GLSL_pow(float *x, float *y, float *ret)
{
    *ret = std::pow(*x, *y);
}

int main()
{
    float *fragCoord[2];
    float *fragColor[4];
    for (int i = 0; i < 4; i++)
        fragColor[i] = new float();

    printf("P3\n%d %d\n255\n", int(iResolution[0]), int(iResolution[1]));
    for (float i = iResolution[1] - 1; i >= 0; i--)
    {
        fprintf(stderr, "\r%.3f%%...", 100 * (iResolution[1] - i) / iResolution[1]);
        for (float j = 0; j < iResolution[0]; j++)
        {
            fragCoord[0] = &j;
            fragCoord[1] = &i;
            GLSL_mainImage(&fragColor, &fragCoord);
            for (int k = 0; k < 3; k++)
            {
                printf("%d ", std::max(std::min(255, int(*fragColor[k] * *fragColor[3] * 255)), 0));
            }
        }
    }
}
// "The Drive Home" by Martijn Steinrucken aka BigWings - 2017
// License Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.
// Email:countfrolic@gmail.com Twitter:@The_ArtOfCode
//
// I was looking for something 3d, that can be made just with a point-line distance function.
// Then I saw the cover graphic of the song I'm using here on soundcloud, which is a bokeh traffic
// shot which is a perfect for for what I was looking for.
//
// It took me a while to get to a satisfying rain effect. Most other people use a render buffer for
// this so that is how I started. In the end though, I got a better effect without. Uncomment the
// DROP_DEBUG define to get a better idea of what is going on.
//
// If you are watching this on a weaker device, you can uncomment the HIGH_QUALITY define
//
// Music:
// Mr. Bill - Cheyah (Zefora's digital rain remix)
// https://soundcloud.com/zefora/cheyah
//
// Video can be found here:
// https://www.youtube.com/watch?v=WrxZ4AZPdOQ
//
// Making of tutorial:
// https://www.youtube.com/watch?v=eKtsY7hYTPg
//

extern "C" {
    float cos(float x);
    float sin(float x);
    float floor(float x);
    float pow(float x,float y);
    void mainImage(out vec4 fragColor,in vec2 fragCoord);
}

float mod(float x,float y){
    return x-float(int(x/y))*y;
}

vec2 floor(vec2 x){
    vec2 ans;
    for(int i=0;i<2;++i)ans[i]=floor(x[i]);
    return ans;
}

vec3 floor(vec3 x){
    vec3 ans;
    for(int i=0;i<3;++i)ans[i]=floor(x[i]);
    return ans;
}

float fract(float x){
    return x-floor(x);
}

vec2 fract(vec2 x){
    return x-floor(x);
}

vec3 fract(vec3 x){
    return x-floor(x);
}

vec3 step(vec3 edge,vec3 x){
    vec3 ans;
    for(int i=0;i<3;++i)ans[i]=float(edge[i]<=x[i]);
    return ans;
}

float dot(vec2 x,vec2 y){
    vec2 z=x*y;
    return z[0]+z[1];
}

float dot(vec3 x,vec3 y){
    vec3 z=x*y;
    return z[0]+z[1]+z[2];
}

float dot(vec4 x,vec4 y){
    vec4 z=x*y;
    return z[0]+z[1]+z[2]+z[3];
}

float min(float x,float y){
    return x < y ? x : y;
}

float max(float x,float y){
    return x > y ? x : y;
}

vec4 max(vec4 x,float y){
    vec4 ans;
    for(int i=0;i<4;++i)ans[i]=x[i]>y?x[i]:y;
    return ans;
}

float clamp(float x,float l,float r){
    if(x<l)x=l;
    else if(x>r)x=r;
    return x;
}

float mix(float x, float y, float a){
    return x * (1. - a) + y * a;
}

vec2 mix(vec2 x,vec2 y,float a){
    return x * (1. - a) + y * a;
}

vec3 mix(vec3 x,vec3 y,float a){
    return x * (1. - a) + y * a;
}

float length(vec2 x) {
    return pow(pow(x.x, 2.) + pow(x.y, 2.), .5);
}

float length(vec3 x) {
    return pow(pow(x.x, 2.) + pow(x.y, 2.) + pow(x.z, 2.), .5);
}

vec3 cross(vec3 x, vec3 y) {
    return vec3(
        x[1] * y[2] - y[1] * x[2],
        x[2] * y[0] - y[2] * x[0],
        x[0] * y[1] - y[0] * x[1]
    );
}

float smoothstep(float l,float r,float x){
    float t;
    t=clamp((x-l)/(r-l),0.,1.);
    return t*t*(3.-2.*t);
}

vec3 normalize(vec3 x) {
    return x / length(x);
}

float sign(float x) {
    if (x == 0.) return 0.;
    return x < 0. ? -1. : 1.;
}

const vec3 streetLightCol = vec3(1., .7, .3);
const vec3 headLightCol = vec3(.8, .8, 1.);
const vec3 tailLightCol = vec3(1., .1, .1);

const float iTime = 0.8;
vec3 iResolution = vec3(512, 288, 1);
vec4 iMouse = vec4(220,108,-255,-119);

const float CAM_SHAKE = 1.;
const float LANE_BIAS = .5;

vec3 ro, rd;

float N(float t) {
	return fract(sin(t*10234.324)*123423.23512);
}
vec3 N31(float p) {
    //  3 out, 1 in... DAVE HOSKINS
   vec3 p3 = fract(vec3(p) * vec3(.1031,.11369,.13787));
   p3 += dot(p3, p3.yzx + 19.19);
   return fract(vec3((p3.x + p3.y)*p3.z, (p3.x+p3.z)*p3.y, (p3.y+p3.z)*p3.x));
}
float N2(vec2 p)
{	// Dave Hoskins - https://www.shadertoy.com/view/4djSRW
	vec3 p3  = fract(vec3(p.xyx) * vec3(443.897, 441.423, 437.195));
    p3 += dot(p3, p3.yzx + 19.19);
    return fract((p3.x + p3.y) * p3.z);
}


float DistLine(vec3 ro, vec3 rd, vec3 p) {
	return length(cross(p-ro, rd));
}

vec3 ClosestPoint(vec3 ro, vec3 rd, vec3 p) {
    // returns the closest point on ray r to point p
    return ro + max(0., dot(p-ro, rd))*rd;
}

float Remap(float a, float b, float c, float d, float t) {
	return ((t-a)/(b-a))*(d-c)+c;
}

float BokehMask(vec3 ro, vec3 rd, vec3 p, float size, float blur) {
	float d = DistLine(ro, rd, p);
    float m = smoothstep(size, size*(1.-blur), d);

    m *= mix(.7, 1., smoothstep(.8*size, size, d));

    return m;
}

float SawTooth(float t) {
    return cos(t+cos(t))+sin(2.*t)*.2+sin(4.*t)*.02;
}

float DeltaSawTooth(float t) {
    return 0.4*cos(2.*t)+0.08*cos(4.*t) - (1.-sin(t))*sin(t+cos(t));
}

vec2 GetDrops(vec2 uv, float seed, float m) {

    float t = iTime+m*30.;
    vec2 o = vec2(0.);

    uv.y += t*.05;

    uv *= vec2(10., 2.5)*2.;
    vec2 id = floor(uv);
    vec3 n = N31(id.x + (id.y+seed)*546.3524);
    vec2 bd = fract(uv);

    vec2 uv2 = bd;

    bd -= .5;

    bd.y*=4.;

    bd.x += (n.x-.5)*.6;

    t += n.z * 6.28;
    float slide = SawTooth(t);

    float ts = 1.5;
    vec2 trailPos = vec2(bd.x*ts, (fract(bd.y*ts*2.-t*2.)-.5)*.5);

    bd.y += slide*2.;								// make drops slide down

    float dropShape = bd.x*bd.x;
    dropShape *= DeltaSawTooth(t);
    bd.y += dropShape;								// change shape of drop when it is falling

    float d = length(bd);							// distance to main drop

    float trailMask = smoothstep(-.2, .2, bd.y);				// mask out drops that are below the main
    trailMask *= bd.y;								// fade dropsize
    float td = length(trailPos*max(.5, trailMask));	// distance to trail drops

    float mainDrop = smoothstep(.2, .1, d);
    float dropTrail = smoothstep(.1, .02, td);

    dropTrail *= trailMask;
    o = mix(bd*mainDrop, trailPos, dropTrail);		// mix main drop and drop trail

    return o;
}

void CameraSetup(vec2 uv, vec3 pos, vec3 lookat, float zoom, float m) {
	ro = pos;
    vec3 f = normalize(lookat-ro);
    vec3 r = cross(vec3(0., 1., 0.), f);
    vec3 u = cross(f, r);
    float t = iTime;

    vec2 offs = vec2(0.);
    vec2 dropUv = uv;

    float x = (sin(t*.1)*.5+.5)*.5;
    x = -x*x;
    float s = sin(x);
    float c = cos(x);

    mat2 rot = mat2(c, -s, s, c);

    dropUv = uv*rot;
    dropUv.x += -sin(t*.1)*.5;

    offs = GetDrops(dropUv, 1., m);

    offs += GetDrops(dropUv*1.4, 10., m);
    offs += GetDrops(dropUv*2.4, 25., m);
    //offs += GetDrops(dropUv*3.4, 11.);
    //offs += GetDrops(dropUv*3., 2.);

    float ripple = sin(t+uv.y*3.1415*30.+uv.x*124.)*.5+.5;
    ripple *= .005;
    offs += vec2(ripple*ripple, ripple);
    vec3 center = ro + f*zoom;
    vec3 i = center + (uv.x-offs.x)*r + (uv.y-offs.y)*u;

    rd = normalize(i-ro);
}

vec3 HeadLights(float i, float t) {
    float z = fract(-t*2.+i);
    vec3 p = vec3(-.3, .1, z*40.);
    float d = length(p-ro);

    float size = mix(.03, .05, smoothstep(.02, .07, z))*d;
    float m = 0.;
    float blur = .1;
    m += BokehMask(ro, rd, p-vec3(.08, 0., 0.), size, blur);
    m += BokehMask(ro, rd, p+vec3(.08, 0., 0.), size, blur);

    m += BokehMask(ro, rd, p+vec3(.1, 0., 0.), size, blur);
    m += BokehMask(ro, rd, p-vec3(.1, 0., 0.), size, blur);

    float distFade = max(.01, pow(1.-z, 9.));

    blur = .8;
    size *= 2.5;
    float r = 0.;
    r += BokehMask(ro, rd, p+vec3(-.09, -.2, 0.), size, blur);
    r += BokehMask(ro, rd, p+vec3(.09, -.2, 0.), size, blur);
    r *= distFade*distFade;

    return headLightCol*(m+r)*distFade;
}


vec3 TailLights(float i, float t) {
    t = t*1.5+i;

    float id = floor(t)+i;
    vec3 n = N31(id);

    float laneId = smoothstep(LANE_BIAS, LANE_BIAS+.01, n.y);

    float ft = fract(t);

    float z = 3.-ft*3.;						// distance ahead

    laneId *= smoothstep(.2, 1.5, z);				// get out of the way!
    float lane = mix(.6, .3, laneId);
    vec3 p = vec3(lane, .1, z);
    float d = length(p-ro);

    float size = .05*d;
    float blur = .1;
    float m = BokehMask(ro, rd, p-vec3(.08, 0., 0.), size, blur) +
    			BokehMask(ro, rd, p+vec3(.08, 0., 0.), size, blur);

    float bs = n.z*3.;						// start braking at random distance
    float brake = smoothstep(bs, bs+.01, z);
    brake *= smoothstep(bs+.01, bs, z-.5*n.y);		// n.y = random brake duration

    m += (BokehMask(ro, rd, p+vec3(.1, 0., 0.), size, blur) +
    	BokehMask(ro, rd, p-vec3(.1, 0., 0.), size, blur))*brake;

    float refSize = size*2.5;
    m += BokehMask(ro, rd, p+vec3(-.09, -.2, 0.), refSize, .8);
    m += BokehMask(ro, rd, p+vec3(.09, -.2, 0.), refSize, .8);
    vec3 col = tailLightCol*m*ft;

    float b = BokehMask(ro, rd, p+vec3(.12, 0., 0.), size, blur);
    b += BokehMask(ro, rd, p+vec3(.12, -.2, 0.), refSize, .8)*.2;

    vec3 blinker = vec3(1., .7, .2);
    blinker *= smoothstep(1.5, 1.4, z)*smoothstep(.2, .3, z);
    blinker *= clamp(sin(t*200.)*100., 0., 1.);
    blinker *= laneId;
    col += blinker*b;

    return col;
}

vec3 StreetLights(float i, float t) {
	 float side = sign(rd.x);
    float offset = max(side, 0.)*(1./16.);
    float z = fract(i-t+offset);
    vec3 p = vec3(2.*side, 2., z*60.);
    float d = length(p-ro);
	float blur = .1;
    vec3 rp = ClosestPoint(ro, rd, p);
    float distFade = Remap(1., .7, .1, 1.5, 1.-pow(1.-z,6.));
    distFade *= (1.-z);
    float m = BokehMask(ro, rd, p, .05*d, blur)*distFade;

    return m*streetLightCol;
}

vec3 EnvironmentLights(float i, float t) {
	float n = N(i+floor(t));

    float side = sign(rd.x);
    float offset = max(side, 0.)*(1./16.);
    float z = fract(i-t+offset+fract(n*234.));
    float n2 = fract(n*100.);
    vec3 p = vec3((3.+n)*side, n2*n2*n2*1., z*60.);
    float d = length(p-ro);
	float blur = .1;
    vec3 rp = ClosestPoint(ro, rd, p);
    float distFade = Remap(1., .7, .1, 1.5, 1.-pow(1.-z,6.));
    float m = BokehMask(ro, rd, p, .05*d, blur);
    m *= distFade*distFade*.5;

    m *= 1.-pow(sin(z*6.28*20.*n)*.5+.5, 20.);
    vec3 randomCol = vec3(fract(n*-34.5), fract(n*4572.), fract(n*1264.));
    vec3 col = mix(tailLightCol, streetLightCol, fract(n*-65.42));
    col = mix(col, randomCol, n);
    return m*col*.2;
}

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
	float t = iTime;
    vec3 col = vec3(0.);
    vec2 uv = fragCoord.xy / iResolution.xy; // 0 <> 1

    uv -= .5;
    uv.x *= iResolution.x/iResolution.y;

    vec2 mouse = iMouse.xy/iResolution.xy;

    vec3 pos = vec3(.3, .15, 0.);

    float bt = t * 5.;
    float h1 = N(floor(bt));
    float h2 = N(floor(bt+1.));
    float bumps = mix(h1, h2, fract(bt))*.1;
    bumps = bumps*bumps*bumps*CAM_SHAKE;

    pos.y += bumps;
    float lookatY = pos.y+bumps;
    vec3 lookat = vec3(0.3, lookatY, 1.);
    vec3 lookat2 = vec3(0., lookatY, .7);
    lookat = mix(lookat, lookat2, sin(t*.1)*.5+.5);

    uv.y += bumps*4.;
    CameraSetup(uv, pos, lookat, 2., mouse.x);

    t *= .03;
    t += mouse.x;

    // fix for GLES devices by MacroMachines
	const float stp = 1./8.;

    for(float i=0.; i<1.; i+=stp) {
       col += StreetLights(i, t);
    }

    for(float i=0.; i<1.; i+=stp) {
        float n = N(i+floor(t));
    	col += HeadLights(i+n*stp*.7, t);
    }

    for(float i=0.; i<1.; i+=stp) {
       col += EnvironmentLights(i, t);
    }

    col += TailLights(0., t);
    col += TailLights(.5, t);

    col += clamp(rd.y, 0., 1.)*vec3(.6, .5, .9);

	fragColor = vec4(col, 1.);
}
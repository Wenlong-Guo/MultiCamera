#extension GL_OES_EGL_image_external : require
precision mediump float;

uniform samplerExternalOES vTexture;
varying vec2 aCoord;

void main()
{
//    float x = mCoord.x;
//    if (x < 0.5){
//        x += 0.25;
//    } else {
//        x-=0.25;
//    }
//    gl_FragColor = texture2D(vTexture, vec2(x, aCoord.y));
    gl_FragColor = texture2D(vTexture,  aCoord);
}

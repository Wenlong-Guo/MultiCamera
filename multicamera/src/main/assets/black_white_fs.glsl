#extension GL_OES_EGL_image_external : require
precision mediump float;
uniform samplerExternalOES vTexture;
varying vec2 textureCoordinate;
varying vec2 aCoord;

void main() {
    vec4 vCameraColor = texture2D(vTexture, aCoord);
    float fGrayColor = (0.3*vCameraColor.r + 0.59*vCameraColor.g + 0.11*vCameraColor.b);
    gl_FragColor = vec4(fGrayColor, fGrayColor, fGrayColor, 1.0);
}

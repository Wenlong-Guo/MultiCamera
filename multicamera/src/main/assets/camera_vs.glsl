attribute vec4 vPosition;
attribute vec2 vCoord;
uniform mat4 vMatrix;
uniform mat4 vMatrixCoord;

varying vec2 aCoord;

attribute vec4 inputTextureCoordinate;
varying vec2 textureCoordinate;

void main()
{
    gl_Position = vPosition * vMatrix;
    aCoord = vCoord;
    textureCoordinate = inputTextureCoordinate.xy;
}


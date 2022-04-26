attribute vec4 vPosition;
attribute vec2 vCoord ;
uniform mat4 vMatrix;

varying vec2 aCoord;
void main()
{
    gl_Position = vPosition * vMatrix;
    aCoord = vCoord;
//    aCoord = (vMatrix * vec4(vCoord,1.0,1.0)).xy;
}


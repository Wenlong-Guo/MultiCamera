attribute vec4 vPosition;
attribute vec2 vCoord ;

varying vec2 vCoord;
uniform mat4 vMatrix;
void main()
{
    gl_Position = vPosition;
//    aCoord = vCoord;
    aCoord = (vMatrix * vec4(vCoord,1.0,1.0)).xy;
}


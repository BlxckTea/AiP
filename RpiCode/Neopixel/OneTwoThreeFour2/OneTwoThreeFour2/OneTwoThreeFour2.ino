//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
// NeoPixel Ring simple sketch (c) 2013 Shae Erisson
// released under the GPLv3 license to match the rest of the AdaFruit NeoPixel library
//
// www.ArduinoPLUS.cc
//
#include <Adafruit_NeoPixel.h>
#define NUMPIXELS      4    // 연결된 Rainbow LED 수

#define PIN            6    // Rainbow LED 를 연결할 디지털 포트 번호 입니다.

// When we setup the NeoPixel library, we tell it how many pixels, and which pin to use to send signals.
// Note that for older NeoPixel strips you might need to change the third parameter--see the strandtest
// example for more information on possible values.

Adafruit_NeoPixel pixels = Adafruit_NeoPixel(NUMPIXELS, PIN, NEO_GRB + NEO_KHZ800);

int delayval = 500;    // 0.5 초 지연시간

void setup() {
  pixels.begin();      // 라이브러리를 초기화 합니다.
  // 아두이노와 첫번째로 연결된 Rainbow LED가 0 번 그 다음이 1번, 2번, ... 이 됩니다.
  // 한개의 Rainbow LED는 Green, Red, Blue의 순서로 색이 지정되며 지정 최대 255, 255, 255 까지 설정할 수 있습니다. 
}

void loop() {

    // Rainbow LED를 순차적으로 점등 합니다.
    pixels.setPixelColor(0, pixels.Color(255,255,255)); // 힌색 (White)
    pixels.show(); 
    delay(delayval); 
    pixels.setPixelColor(1, pixels.Color(255,0,0));     // 녹색 (Green)
    pixels.show(); 
    delay(delayval); 
    pixels.setPixelColor(2, pixels.Color(0,255,0));     // 빨간색 (Red)
    pixels.show(); 
    delay(delayval); 
    pixels.setPixelColor(3, pixels.Color(0,0,255));     // 파란색 (Blue)
    pixels.show(); 
    delay(delayval);

    // Rainbow LED를 순차적으로 소등 합니다.
    pixels.setPixelColor(0, pixels.Color(0,0,0)); 
    pixels.show(); 
    delay(delayval);
    pixels.setPixelColor(1, pixels.Color(0,0,0)); 
    pixels.show(); 
    delay(delayval);
    pixels.setPixelColor(2, pixels.Color(0,0,0)); 
    pixels.show(); 
    delay(delayval);
    pixels.setPixelColor(3, pixels.Color(0,0,0)); 
    pixels.show(); 
    delay(delayval);

}

//////////////////////////////////////////////////////////////////////////////////////////////////////




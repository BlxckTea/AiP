from Variable import *

def wheel(pos):
    # Input a value 0 to 255 to get a color value.
    # The colours are a transition r - g - b - back to r.
    if pos < 0 or pos > 255:
        r = 255
        g = 0
        b = 0
    elif pos < 85:
        r = int(pos * 3)
        g = int(255 - pos*3)
        b = 0
    elif pos < 170:
        pos -= 85
        r = int(255 - pos*3)
        g = 0
        b = int(pos*3)
    else:
        pos -= 170
        r = 0
        g = int(pos*3)
        b = int(255 - pos*3)
    return (r, g, b) if ORDER == neopixel.RGB or ORDER == neopixel.GRB else (r, g, b, 0)


def rainbow_cycle(wait):
    for j in range(255):
        for i in range(num_pixels):
            pixel_index = (i * 256 // num_pixels) + j
            pixels[i] = wheel(pixel_index & 255)
        pixels.show()
        time.sleep(wait)


def sunrise():
    #for j in range(num_pixels):
        #pixels[j] = (0,0,50)
    #pixels.show()
    #time.sleep(2)

    #for j in range(num_pixels):
        #pixels[j] = (232,53,35)
    #pixels.show()
    #time.sleep(5)

    #for j in range(num_pixels):
        #pixels[j] = (233,70,48)
    #pixels.show()
    #time.sleep(5)

    #for j in range(num_pixels):
        #pixels[j] = (235,94,41)
    #pixels.show()
    #time.sleep(5)

    #for j in range(num_pixels):
        #pixels[j] = (243,162,115)
    #pixels.show()
    #time.sleep(5)

    
    for j in range(num_pixels):
        pixels[j] = (255,255,255)
    pixels.show()
    

def test():
    pixels.setPixelColor(0,Color(255,0,0))
    pixels.setPixelColor(1,Color(255,0,0))
    pixels.show()
while True:
    sunrise()
    #test()
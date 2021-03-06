from Variable import *


def wheel(pos):
    # Input a value 0 to 255 to get a color value.
    # The colours are a transition r - g - b - back to r.
    if pos < 0 or pos > 255:
        r = g = b = 0
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
        #pixels[j] = (231,31,23)
    #pixels.show()
    #time.sleep(5)

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
    sleep(1)

def vibration():
    while True:
        
        GPIO.output(in1, GPIO.HIGH)
        GPIO.output(in2, GPIO.LOW)

        sleep(1)
        GPIO.cleanup()


def detect_Button(btn_status):

    # btn_status = False
    #아케이드 버튼이 눌리지 않았으면 기다리기
    #아케이드 버튼이 눌려서 GPIO.input(5)값이 0이 되면 
    #파이어베이스에 alarmSwitch와 measureSwitch를 False로 바꾸고 빠져나가기
    while True:
        if GPIO.input(5) == 0 :
            firebase.put('/switch','alarmSwitch','False')
            firebase.put('/switch','measureSwitch','False')
            break
    
def make_Alarm() :

    alarmSwitch = firebase.get('/switch/alarmSwitch', None)

    print("make_Alarm에 들어옴")

    if (alarmSwitch == "True") :
        print("알람시작")
        #led on
        sunrise()
        #진동모터 on
        GPIO.output(in1, GPIO.HIGH)
        GPIO.output(in2, GPIO.LOW)
        #음악 on
        pygame.mixer.music.play()
        
        #아케이드 버튼이 눌리는지 확인하는 함수
        #이 함수에서 돌아왔다는 뜻은 아케이드 버튼이 눌렸고
        #파이어베이스에 alarmSwitch가 False가 됐다는 뜻
        detect_Button(False)
        #sleep(3)
        print("알람 끝")

        #alarmSwitch 가 꺼지면 진동모터 off
        GPIO.cleanup()
        #led off
        for j in range(num_pixels):
            pixels[j] = (0,0,0)
        pixels.show()
        #노래 off
        pygame.mixer.music.stop()


from Variable import *

pygame.mixer.init()
pygame.mixer.music.load("./sleepMusic.mp3")
pygame.mixer.music.set_volume(1.0)

def detect_Button(btn_status):

    # btn_status = False
    #아케이드 버튼이 눌리지 않았으면 기다리기
    #아케이드 버튼이 눌려서 GPIO.input(5)값이 0이 되면 
    #파이어베이스에 alarmSwitch와 measureSwitch를 False로 바꾸고 빠져나가기
    while True:
        if GPIO.input(5) == 0 :
            firebase.put('/switch','sleepMusic','False')
            break

while True:
	measureSwitch = firebase.get('/switch/measureSwitch', None)
	sleepMusic = firebase.get('/switch/sleepMusic', None)

	if(measureSwitch == "False") and (sleepMusic == "True"):
		pygame.mixer.music.play()
		detect_Button(False)
		pygame.mixer.music.stop()

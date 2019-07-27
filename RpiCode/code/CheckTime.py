from Alarm import make_Alarm
from Variable import *

#measureSwitch 확인하기 


#refine 12hour format to 24 hour format
def refine_Hour(raw_hour, AP) :

	#00시 에서 '시' 빼고 숫자만 남기기
    index = raw_hour.find("시")
    server_hour = raw_hour[0:index]
    
    #오전이고 한자리수면 앞에 0붙이기
    #오전 12시는 00시로 바꿔주기
    if AP == "오전":
        if len(server_hour) == 1 :
            server_hour = '0'+server_hour

        if server_hour == "12":
        	server_hour = "00"

    #오후면 12시간씩 늘려주고 오후 12시는 12시 그대로
    else :
        if server_hour == "12" :
            return server_hour
        
        #12시간씩 늘려주기
        server_hour = str(int(server_hour) + 12)

    return server_hour

def refine_Minute(raw_minutes) :

	if raw_minutes == "":
		server_minutes = "00"
	else :
		index = raw_minutes.find("분")
		server_minutes = raw_minutes[0:index]

	return server_minutes

def check_Time (rangeStart, rangeEnd):
	
	while True:
		#현재 시간
		current = datetime.datetime.now()

		#현재 시간이 기상범위 사이에 있으면 상태 확인하기
		if (rangeStart <= current <= rangeEnd):
			#사용자 수면 상태 가져오기
			state = firebase.get('/model/state', None)
			#만약에 사용자가 REM 상태면 알람 스위치 키고 알람 울리기	
			if state == "REM" :
				print("현재 시간이 기상 범위 안에 있고 현재 수면 상태가 REM 입니다.")
				print("알람을 시작합니다.")
				firebase.put('/switch','alarmSwitch','True')
				make_Alarm()	
				break

		#현재 시간이 설정시간보다 크거나 같아지면 
		#상태 상관 없이 알람 스위치 키고알람 울리기
		elif current >= rangeEnd :
			firebase.put('/switch','alarmSwitch','True')
			make_Alarm()
			break
		else :
			sleep(5)

def main():
	#read the day or night
	AP = firebase.get('/data/AP', None)

	#read the hour from firebase and refine to 24 hour format
	# server_hour, server_minutes 둘 다 str임
	server_hour = refine_Hour(firebase.get('/data/hour', None),AP)
	server_minutes = refine_Minute(firebase.get('/data/minutes', None))


	#사용자가 설정한 시간이 범위 끝
	rangeEnd = datetime.datetime.now().replace(hour = int(server_hour), minute = int(server_minutes), second = 0, microsecond = 0)
	#사용자가 설정한 시간 30분 전이 범위 시작
	rangeStart = rangeEnd - datetime.timedelta(minutes=30)

	#파베에서 읽어온 값이랑 기상범위 확인해보는 print임
	#print(AP + " "+ server_hour + "시 " + server_minutes + "분")
	#print("rangeStart : " + str(rangeStart))
	#print("rangeEnd : " + str(rangeEnd))

	check_Time(rangeStart, rangeEnd)



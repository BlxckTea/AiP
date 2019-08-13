# -*- coding: utf-8 -*- 

#파이어베이스 모듈
from firebase import firebase
from firebase_admin import credentials
from firebase_admin import firestore
from firebase_admin import db

import numpy as np

from model import model

temp1 = ""
temp2 = ""
temp3 = ""
final_state = ""
count = 1

firebase = firebase.FirebaseApplication("https://aip-rpi.firebaseio.com/")
ref = db.reference('/model')

postingMode = True
postId = 0


#읽어온 값 numpy 배열로 변환하고 모델 돌리기
#결과값 파베에 쓰기
def tonumpy(value):
	global count
	global temp1
	global temp2
	global temp3
	global final_state

	print("tonumpy 안에 들어옴")
	count = count+1

	#string 으로부터 컴마를 구분자로 해서 1,270짜리 float numpy 배열 만들기
	narray = np.fromstring(value,dtype =float, count=270, sep=',') #마이너스 나옴
	#narray = np.fromstring(value ,dtype =float, count=9) #마이너스 안나옴

	#케라스 모델에 넘기고 return 값 앞뒤에 [] 빼기
	axisTen = model(value)[1:-1]

	path_state = '/model/statelist' + postId
	#state 단계 나누기
	if 0.0 < float(axisTen) < 2.5 :
		state = "DEEP"
	elif 2.5 < float(axisTen) < 4.8 :
		state = "LIGHT"
	elif 5.0 < float(axisTen) < 8.0 :
		state = "REM"
	else :
		state = "AWAKE"

	if count == 1:
		temp1 = state
	elif count == 2 :
		temp2 = state
	elif count == 3 :
		temp3 = state
	else :
		if(temp1 == temp2) :
			final_state = temp1
			temp1 = temp2
			temp2 = temp3
			temp3 = state
		elif (temp1 != temp2):
			final_state = temp1
			if(temp1 == temp3):
				temp2 = temp1
			temp1 = temp2
			temp2 = temp3
			temp3 = state

		firebase.put('/model','state', final_state)

		#파베에 statelist 붙여나가기
		statelist = firebase.get(path_state, None) + final_state + ","
		firebase.put(path_state, axislist)


	#파베에 10값 쓰기
	firebase.put('/model','axisTen', axisTen)

	#파베에 axislist 붙여나가기
	path_axis = '/model/axislist' + postId
	axislist = firebase.get(path_axis, None) + axisTen + ","
	firebase.put(path_axis, axislist)


#파베에서 mpu6050 값 읽어오기
def read_value(count):
	count = str(count)
	path = "/model/mpu6050/" + count

	value = firebase.get(path, None)

	#value 갱신이 안됐으면 1분 기다렸다가 다시 확인
	#value 들어오면 while문 끝내기
	while value == "":
		sleep(60)
		value = firebase.get(path, None)

	#읽고 지우기
	#firebase.put('/model/mpu6050',count,'')

	#앞에 시간이랑 날짜 자르기
	value = value[21:-2]
	tonumpy(value)

def read_value1():

	value = np.array([0.027099609,0.022949219,0.020507813,0.015380859,0.003173828,0.003173828,-0.015869141,0.030029297,1.120361328,0.028320313,0.027099609,0.032470703,0.010498047,0.007080078,0.008544922,-0.015869141,0.030273438,1.124755859,0.028808594,0.022216797,0.027099609,0.000732422,0.017089844,0.013183594,-0.016357422,0.030761719,1.122070313,0.029296875,0.030761719,0.038085938,0.006103516,0.005126953,0.004638672,-0.015869141,0.030761719,1.124511719,0.024902344,0.028076172,0.027099609,0.014160156,0.017333984,0.004638672,-0.016113281,0.030761719,1.123779297,0.028320313,0.029296875,0.034179688,0.005126953,0.001708984,0.0078125,-0.016357422,0.030029297,1.124755859,0.026855469,0.025390625,0.027099609,0.013671875,0.000732422,0.006591797,-0.015869141,0.029296875,1.124511719,0.028076172,0.028076172,0.026123047,0.016357422,0.004394531,0.017089844,-0.016113281,0.028320313,1.123291016,0.030029297,0.030273438,0.028564453,0.008056641,0.006103516,0.002441406,-0.015869141,0.031005859,1.124755859,0.029296875,0.025146484,0.030273438,0.006103516,0.006347656,0.004394531,-0.015869141,0.030761719,1.118896484,0.028564453,0.029541016,0.026855469,0.00390625,0.004150391,0.005615234,-0.016601563,0.031005859,1.124267578,0.058837891,0.141357422,0.070556641,0.014404297,0.005859375,0.007568359,0.046630859,0.158447266,1.133300781,0.026611328,0.027099609,0.025390625,0.010498047,0.009033203,0.008300781,0.059326172,0.045654297,1.123535156,0.019287109,0.018798828,0.031005859,0.010986328,0.014648438,0.000488281,0.062255859,0.046142578,1.124755859,0.029052734,0.018554688,0.021972656,0.000732422,0.000244141,0.009765625,0.062255859,0.046630859,1.124755859,0.028320313,0.025390625,0.109130859,0.009033203,0.014892578,0.000976563,0.062255859,0.046630859,1.214599609,0.024414063,0.019287109,0.028076172,0.003417969,0.004394531,0.003417969,0.062255859,0.045898438,1.124511719,0.023681641,0.021484375,0.030273438,0.005615234,0.00390625,0.008300781,0.061523438,0.046630859,1.124511719,0.028808594,0.026123047,0.037841797,0.009765625,0.010253906,0.000976563,0.061767578,0.046630859,1.124755859,0.030761719,0.01953125,0.028564453,0.008789063,0.004150391,0.003417969,0.062255859,0.044189453,1.124755859,0.029785156,0.013183594,0.028076172,0,0.004882813,0.004150391,0.061767578,0.046630859,1.123291016,0.028320313,0.023681641,0.025146484,0.014404297,0.006103516,0.018554688,0.062011719,0.046630859,1.124023438,0.029296875,0.023925781,0.026611328,0.008544922,0.003662109,0.004882813,0.061035156,0.046142578,1.124511719,0.025390625,0.017578125,0.037109375,0.002929688,0.001708984,0.017578125,0.060302734,0.046630859,1.124511719,0.045410156,0.053710938,0.102783203,0.010253906,0,0.006347656,0.062255859,0.058105469,1.205078125,0.027099609,0.026123047,0.024414063,0.010742188,0.000976563,0.005615234,0.062255859,0.060302734,1.121337891,0.022216797,0.038330078,0.033447266,0.001464844,0.004882813,0.014648438,0.059082031,0.056396484,1.124755859,0.02734375,0.031005859,0.024169922,0.011962891,0.000732422,0.007568359,0.062255859,0.046630859,1.122558594,0.026855469,0.034179688,0.039794922,0.018798828,0.007324219,0.002441406,0.061767578,0.046630859,1.137451172,0,0,0,0,0,0,0,0,0])
	#정답: 1.8103913
	tonumpy(value)

def read_value2():

	value = np.array([0.304931641,0.36328125,0.449462891,0.027587891,0.004394531,0.002197266,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.010498047,0.006591797,0,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.007568359,0,0.002685547,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.003662109,0.011474609,0.008544922,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.004150391,0.007324219,0.003662109,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.001953125,0.004638672,0.005371094,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.005615234,0.005126953,0.006591797,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.003417969,0.004394531,0.008300781,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.017089844,0.006347656,0.00390625,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.005371094,0.003417969,0.005126953,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.008056641,0.003662109,0.010498047,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.000244141,0.008544922,0.003662109,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.004638672,0.000488281,0.000488281,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.0078125,0.004882813,0.010986328,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.017333984,0.002197266,0.0078125,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.021484375,0.009277344,0.002197266,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.006591797,0.003173828,0.000732422,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.002929688,0.005615234,0.007324219,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.002685547,0.003417969,0.005371094,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.000488281,0.013427734,0.020996094,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.006347656,0.000732422,0.007324219,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.006591797,0.005371094,0.006103516,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.010986328,0.041748047,0.019042969,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.015869141,0.015136719,0.014892578,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.001464844,0.014648438,0.005126953,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.008300781,0.020263672,0.024169922,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.013916016,0.002929688,0.000488281,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.008300781,0.008789063,0.001464844,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.008544922,0.013671875,0.003173828,0.153564453,0.272216797,1.556396484,0,0,0,0,0,0,0,0,0])
	#정답: 1.8103913
	tonumpy(value)

def read_value3():

	value = np.array([0.304931641,0.36328125,0.449462891,0.027587891,0.004394531,0.002197266,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.010498047,0.006591797,0,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.007568359,0,0.002685547,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.003662109,0.011474609,0.008544922,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.004150391,0.007324219,0.003662109,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.001953125,0.004638672,0.005371094,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.005615234,0.005126953,0.006591797,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.003417969,0.004394531,0.008300781,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.017089844,0.006347656,0.00390625,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.005371094,0.003417969,0.005126953,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.008056641,0.003662109,0.010498047,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.000244141,0.008544922,0.003662109,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.004638672,0.000488281,0.000488281,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.0078125,0.004882813,0.010986328,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.017333984,0.002197266,0.0078125,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.021484375,0.009277344,0.002197266,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.006591797,0.003173828,0.000732422,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.002929688,0.005615234,0.007324219,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.002685547,0.003417969,0.005371094,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.000488281,0.013427734,0.020996094,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.006347656,0.000732422,0.007324219,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.006591797,0.005371094,0.006103516,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.010986328,0.041748047,0.019042969,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.015869141,0.015136719,0.014892578,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.001464844,0.014648438,0.005126953,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.008300781,0.020263672,0.024169922,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.013916016,0.002929688,0.000488281,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.008300781,0.008789063,0.001464844,0.153564453,0.272216797,1.556396484,0.304931641,0.36328125,0.449462891,0.008544922,0.013671875,0.003173828,0.153564453,0.272216797,1.556396484,0,0,0,0,0,0,0,0,0])
	#정답: 1.8103913
	tonumpy(value)

def createIdOnce():
	postingMode
	#get last postId from db
	snapshot = ref.order_by_child('axislist').limit_to_last(1).get()
	for key, val in snapshot:
	    postId = key #update value
	    # print('{0}: {1}'.format(key, val)) #check
	postId = postId + 1
	postingMode = False #locking the create a new id


while True:
	if firebase.get('/switch/measureSwitch', None) == "True":
		if postingMode == True:
			createIdOnce()
		# read_value1()
		# read_value2()
		# read_value3()
		read_value(1)
		read_value(2)
		read_value(3)
	else:
		postingMode = True #unlocking the create a new id

	
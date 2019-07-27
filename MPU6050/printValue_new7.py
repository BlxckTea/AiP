'''
	190313 날짜포함하여 파일기록완료
  		 차이 절대값으로 하여 저장
         190314 시간 계산해봤더니 5:14 초에 한번씩 찍어서
         time.time() 메소드로 수정
         
	190320 save time changed (5min->1min)
	190327 revise diff value

'''
import smbus					#import SMBus module of I2C
from time import sleep          #import
import sys						#파일 입출력

import threading #일정시간 주기위함
from datetime import datetime
import time

#some MPU6050 Registers and their Address
PWR_MGMT_1   = 0x6B
SMPLRT_DIV   = 0x19
CONFIG       = 0x1A
GYRO_CONFIG  = 0x1B
INT_ENABLE   = 0x38
ACCEL_XOUT_H = 0x3B
ACCEL_YOUT_H = 0x3D
ACCEL_ZOUT_H = 0x3F
GYRO_XOUT_H  = 0x43
GYRO_YOUT_H  = 0x45
GYRO_ZOUT_H  = 0x47


bus = smbus.SMBus(1) 	# or bus = smbus.SMBus(0) for older version boards
Device_Address = 0x68   # MPU6050 device address

def MPU_Init():
	bus.write_byte_data(Device_Address, SMPLRT_DIV, 7) # write to sample rate register
	bus.write_byte_data(Device_Address, PWR_MGMT_1, 1) # Write to power management register	
	bus.write_byte_data(Device_Address, CONFIG, 0) # Write to Configuration register
	bus.write_byte_data(Device_Address, GYRO_CONFIG, 24) # Write to Gyro configuration register
	bus.write_byte_data(Device_Address, INT_ENABLE, 1) # Write to interrupt enable register

def read_raw_data(addr):
	# Accelero and Gyro value are 16-bit
    high = bus.read_byte_data(Device_Address, addr)
    low = bus.read_byte_data(Device_Address, addr+1)

    # concatenate higher and lower value
    value = ((high << 8) | low)
    
    # to get signed value from mpu6050
    if(value > 32768):
        value = value - 65536
    return value
    
# init
max_x = 0.0 # max x,y,z
max_y = 0.0
max_z = 0.0
old_x = read_raw_data(ACCEL_XOUT_H) / 16384.0
old_y = read_raw_data(ACCEL_YOUT_H) / 16384.0
old_z = read_raw_data(ACCEL_ZOUT_H) / 16384.0
rm_x = -2.0 # RawMax
rm_y = -2.0
rm_z = -2.0
count = 0;
txt = "" 

#def saveText(max_x, max_y, max_z, diff_x, diff_y, diff_z, rm_x, rm_y, rm_z):
#	print("### Save start ###")
#
#	now = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
#	data = " %.10f, " % max_x + "%.10f, " % max_y + "%.10f " % max_z + "%.10f, " % diff_x + "%.10f, " % diff_y + "%.10f " % diff_z + "%.10f, " % rm_x + "%.10f, " % rm_y + "%.10f " % rm_z
#	str = now + data + '\n'
#
#	print("### Save success ###")


MPU_Init()


#threading.Timer(3, savetext, args=[mAx, mAy, mAz]).start() # 60초 간격으로 savetext함수실행
start = time.time()
with open("./newMD.txt" , "a+") as fd:

	while True:
		#print("FIND max variation")

		# Read Accelerometer raw value
		if count == 0 :
			raw_x = old_x
			raw_y = old_y
			raw_z = old_z
			count += 1

		else :
			raw_x = read_raw_data(ACCEL_XOUT_H) / 16384.0
			raw_y = read_raw_data(ACCEL_YOUT_H) / 16384.0
			raw_z = read_raw_data(ACCEL_ZOUT_H) / 16384.0

		#print([raw_x, raw_y, raw_z]) # 현재 측정값 확인

		# RawMax
		if raw_x > rm_x : rm_x = raw_x
		if raw_y > rm_y : rm_y = raw_y
		if raw_z > rm_z : rm_z = raw_z
		#print([rm_x, rm_y, rm_z]) # 현재 측정최대값 확인

		# 현재-이전값 변화량 절대값 만들기
		diff_x = abs(raw_x-old_x)
		diff_y = abs(raw_y-old_y)
		diff_z = abs(raw_z-old_z)
		#print([diff_x, diff_y, diff_z]) # 변화량 확인

		# 새 값이 최대값보다 크면 최대값 바꾸기
		if diff_x > max_x : max_x = diff_x
		if diff_y > max_y : max_y = diff_y
		if diff_z > max_z : max_z = diff_z
		#print([max_x, max_y, max_z]) # 변화량 확인

		old_x = raw_x
		old_y = raw_y
		old_z = raw_z


		stop = time.time()
		sleep(0.1) # 0.1초간격으로 측정


		if(count == 31) :
			count = 1
			#fd = open("./newMD.txt" , "a+")
			txt = "$" + txt
			fd.write(txt)
			txt = ""
			print("5min")
			
		if (stop - start ) >= 10.0 and (stop - start) < 10.1:
			now = datetime.now().strftime('%Y-%m-%d %H:%M:%S ')
			data = "%.10f, " % max_x + "%.10f, " % max_y + "%.10f " % max_z + "%.10f, " % diff_x + "%.10f, " % diff_y + "%.10f " % diff_z + "%.10f, " % rm_x + "%.10f, " % rm_y + "%.10f " % rm_z
			txt = txt + now + data + '\n'
			print("10sec")
			start = time.time()
			max_x = 0.0
			max_y = 0.0
			max_z = 0.0
			old_x = read_raw_data(ACCEL_XOUT_H) / 16384.0
			old_y = read_raw_data(ACCEL_YOUT_H) / 16384.0
			old_z = read_raw_data(ACCEL_ZOUT_H) / 16384.0
			rm_x = -2.0
			rm_y = -2.0
			rm_z = -2.0
			count += 1


		#print("END max variation")

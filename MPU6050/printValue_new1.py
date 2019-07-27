'''
  Read Gyro and Accelerometer by Interfacing Raspberry Pi with MPU6050 using Python
	http://www.electronicwings.com
'''
import smbus					#import SMBus module of I2C
from time import sleep          #import
import sys						#파일 입출력

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
mAx = 0
mAy = 0
mAz = 0
count = 0


def MPU_Init():
	#write to sample rate register
	bus.write_byte_data(Device_Address, SMPLRT_DIV, 7)
	
	#Write to power management register
	bus.write_byte_data(Device_Address, PWR_MGMT_1, 1)
	
	#Write to Configuration register
	bus.write_byte_data(Device_Address, CONFIG, 0)
	
	#Write to Gyro configuration register
	bus.write_byte_data(Device_Address, GYRO_CONFIG, 24)
	
	#Write to interrupt enable register
	bus.write_byte_data(Device_Address, INT_ENABLE, 1)

def read_raw_data(addr):
	#Accelero and Gyro value are 16-bit
        high = bus.read_byte_data(Device_Address, addr)
        low = bus.read_byte_data(Device_Address, addr+1)
    
        #concatenate higher and lower value
        value = ((high << 8) | low)
        
        #to get signed value from mpu6050
        if(value > 32768):
                value = value - 65536
        return value


bus = smbus.SMBus(1) 	# or bus = smbus.SMBus(0) for older version boards
Device_Address = 0x68   # MPU6050 device address

MPU_Init()

print (" Reading Data of Gyroscope and Accelerometer")

while True:
	count=0

	while count<60.0 :
	
		#Read Accelerometer raw value
		acc_x = read_raw_data(ACCEL_XOUT_H)
		acc_y = read_raw_data(ACCEL_YOUT_H)
		acc_z = read_raw_data(ACCEL_ZOUT_H)
	
		#Read Gyroscope raw value
		#gyro_x = read_raw_data(GYRO_XOUT_H)
		#gyro_y = read_raw_data(GYRO_YOUT_H)
		#gyro_z = read_raw_data(GYRO_ZOUT_H)
	
		#Full scale range +/- 250 degree/C as per sensitivity scale factor
		#16384 =  1g 얘가 중력가속도
		#이센서는 최대 2g까지 표현  (-32768~32767)
		#1000ms = 1s
		Ax = acc_x/16384.0
		Ay = acc_y/16384.0
		Az = acc_z/16384.0
		print("Gx=%.2f" % Ax,"Gy=%.2f" %Ay, "Gz=%.2f" %Az)		#일초에 한번 측정값 출력

		#새 측정값이 최대값보다 크면 최대값 바꾸기
		if Ax > mAx : mAx=Ax
		if Ay > mAy : mAy=Ay
		if Az > mAz : mAz=Az

		count+=0.1
		sleep(0.1)
	
		#1도/s = 131
		#초당 움직인 속도
		#Gx = gyro_x/131.0
		#Gy = gyro_y/131.0
		#Gz = gyro_z/131.0

	
	print("Gx=%.2f" % mAx,"Gy=%.2f" %mAy, "Gz=%.2f" %mAz)

	f = open("./data.txt", 'a')
	raw_data = "%.2f, " % mAx + "%.2f, " % mAy + "%.2f" % mAz
	f.write(raw_data + '\n')



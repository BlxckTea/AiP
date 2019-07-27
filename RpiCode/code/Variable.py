from ctypes import *

#시간 모듈
from time import *
import datetime
import time
from time import sleep
import threading #일정시간 주기위함

#파이어베이스 모듈
import firebase_admin
from firebase import firebase
from firebase_admin import credentials
from firebase_admin import firestore
from firebase_admin import db

#dc모터 및 네오픽셀 모듈
from board import *
import board
import neopixel
from neopixel import *
import RPi.GPIO as GPIO

#노래 재생 모듈
import argparse
import pygame

#mpu6050 모듈
import smbus					#import SMBus module of I2C

#파일 입출력에 필요한 모듈
import sys

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



#firebase
firebase = firebase.FirebaseApplication("https://aip-rpi.firebaseio.com/")


#neopixel
pixel_pin = board.D18
num_pixels = 40
ORDER = neopixel.GRB

pixels = neopixel.NeoPixel(pixel_pin, num_pixels, brightness=0.2, auto_write=False,
                           pixel_order=ORDER)


# dc motor setting
in1 = 24
in2 = 23
en = 25
temp1=1

GPIO.setmode(GPIO.BCM)
GPIO.setup(in1,GPIO.OUT)
GPIO.setup(in2,GPIO.OUT)
GPIO.setup(en,GPIO.OUT)
GPIO.output(in1,GPIO.LOW)
GPIO.output(in2,GPIO.LOW)
p=GPIO.PWM(en,1000)

#music setting
#pygame.mixer.init()
#pygame.mixer.music.load("./alarm.mp3")
#pygame.mixer.music.set_volume(1.0)


#arcade button setting
GPIO.setup(5, GPIO.IN, pull_up_down=GPIO.PUD_UP)
GPIO.setup(6, GPIO.OUT)

#mpu6050 setting
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
Device_Address = 0x68


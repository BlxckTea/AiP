from Variable import *

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
txt = ""
count = 0

db_cnt = 1


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

def measure(measureSwitch): 

    print("measure 들어옴")

    global count
    global max_x
    global max_y
    global max_z
    global old_x
    global old_y
    global old_z
    global rm_x
    global rm_y
    global rm_z
    global txt
    global db_cnt

    start = time.time()

    with open("./newMD2.txt" , "a+", encoding='utf-8') as fd:

        while measureSwitch == "True" :

            #맨 처음에는 raw에 old값 넣어주고 두번째 부터 old와 raw가 차이남
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
            

            # RawMax 갱신
            if raw_x > rm_x : rm_x = raw_x
            if raw_y > rm_y : rm_y = raw_y
            if raw_z > rm_z : rm_z = raw_z
            #print("raw_x") # 현재 측정최대값 확인

            # 현재-이전값 변화량 절대값 만들기
            diff_x = abs(raw_x-old_x)
            diff_y = abs(raw_y-old_y)
            diff_z = abs(raw_z-old_z)
            #print("diff_x") # 변화량 확인

            # 새 값이 최대값보다 크면 최대값 바꾸기
            # diff_m 갱신
            if diff_x > max_x : max_x = diff_x
            if diff_y > max_y : max_y = diff_y
            if diff_z > max_z : max_z = diff_z
            #print("max_x") # 변화량 확인

            old_x = raw_x
            old_y = raw_y
            old_z = raw_z


            stop = time.time()
            sleep(0.1) # 0.1초간격으로 측정

            #print("측정 완료")

            #5분에 한번 파일에 쓰고 
            #파이어베이스에 쓰기
            if(count == 31) :
                count = 1
                #fd = open("./newMD.txt" , "a+")
                now = datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S ')
                
                txt = "$" + now + txt +'\n'
                fd.write(txt)
                fd.flush()

                print("파일에 쓰기 완료")

                firebase.put('/model/mpu6050',str(db_cnt),txt)
                db_cnt += 1
                if(db_cnt == 4):
                    db_cnt = 1

                txt = ""
                print("5min")
                print("파베에 쓰기 완료")
                
            if (stop - start ) >= 10.0 and (stop - start) < 12.0 :
                data = "%.10f, " % max_x + "%.10f, " % max_y + "%.10f, " % max_z + "%.10f, " % diff_x + "%.10f, " % diff_y + "%.10f, " % diff_z + "%.10f, " % rm_x + "%.10f, " % rm_y + "%.10f, " % rm_z
                txt = txt + data + ","
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
            #측정 스위치 꺼졌는지 확인하기.
            #아케이드 버튼 눌리면 꺼짐.
            measureSwitch = firebase.get('/switch/measureSwitch', None)

def main():

    MPU_Init()

    measureSwitch = firebase.get('/switch/measureSwitch', None)
    print("measureSwitch 읽어옴 " + measureSwitch)

    #파이어베이스에서 measureSwitch를 읽어오고
    #측정 스위치가 True 될 때까지 기다리면서 체크하기
    while measureSwitch == "False" :
        sleep(1)
        measureSwitch = firebase.get('/switch/measureSwitch', None)

    print("measure 부르기")

    #측정 스위치가 True가 되면 루프를 빠져나오고 측정 시작   
    measure(measureSwitch)
    



    

#!/usr/bin/python
#read data from serial
#check threshold for acc
#publish to firebase 

import serial
import syslog
import time
import matplotlib.pyplot as plt
import numpy as np
from firebase import firebase

fb = firebase.FirebaseApplication('https://mamatsav-53669.firebaseio.com/', None)


def running_mean(x, N):
    cumsum = np.cumsum(np.insert(x, 0, 0))
    return (cumsum[N:] - cumsum[:-N]) / N


#The following line is for serial over GPIO
port = '/dev/ttyUSB0'

plt.ion()
fig = plt.figure()
ard = serial.Serial(port,115200,timeout=5)
activity_threshold = 2000
time_threshold_low = 10
time_threshold_mid = 30
time_threshold_high = 45

i = 0
AcX=[0]
AcX_Diff=[]
AcY_Diff=[]
AcZ_Diff=[]

AcY=[0]
AcZ=[0]
SumAbs = []
SumAbsDiff=[]
Sent_Data_low = 0
Sent_Data_mid = 0
Sent_Data_high = 0
Sent_dig_state = 0
Sent_end_state = 0
while True:

    msg = ard.readline()
    Data = [int(s) for s in msg.split() if s.lstrip('-').isdigit()]


    plt.clf()
    if len(SumAbs) > 100:
        AcX.pop(0)
        AcX.append(Data[0])
        AcX_Diff.pop(0)
        AcX_Diff.append(abs(AcX[-1]-AcX[-2])/1)
        AcY_Diff.pop(0)
        AcY_Diff.append(abs(AcY[-1]-AcY[-2])/1)
        AcZ_Diff.pop(0)
        AcZ_Diff.append(abs(AcZ[-1]-AcZ[-2])/1)
        AcY.pop(0)
        AcY.append(Data[1])
        AcZ.pop(0)
        AcZ.append(Data[2])
        SumAbs.pop(0)
        SumAbs.append(abs(Data[0])+abs(Data[1])+abs(Data[2]))

    else:
        SumAbs.append((AcX_Diff+AcY_Diff+AcZ_Diff))
        AcX.append(Data[0])
        AcY.append(Data[1])
        AcZ.append(Data[2])
        AcX_Diff.append(abs(AcX[-1]-AcX[-2])/1)
        AcZ_Diff.append(abs(AcZ[-1]-AcZ[-2])/1)
        AcY_Diff.append(abs(AcY[-1]-AcY[-2])/1)


#    plt.plot(AcX)
#    plt.hold(True)
#    plt.plot(AcY)
#    plt.plot(AcZ)
#    plt.show()
    #plt.ylim((0,4000))
    data_diff_sum = map(lambda x,y,z: x+y+z,AcX_Diff,AcY_Diff,AcZ_Diff)
    data_diff_mean = running_mean(data_diff_sum,1)
    data_diff_np = np.array(data_diff_mean)
    activity = (data_diff_np>activity_threshold)
    activity_low = np.sum(activity)>time_threshold_low
    activity_mid = np.sum(activity)>time_threshold_mid
    activity_high = np.sum(activity)>time_threshold_high
    if activity_low and not Sent_Data_low:
        fb.put('/nest1/','hatchState','2') #"path","property_Name",property_Value
        Sent_Data_low = 1
        print "state: send low (2)"
    if activity_mid and Sent_Data_low and not Sent_Data_mid:
        fb.put('/nest1/','hatchState','3') #"path","property_Name",property_Value
        Sent_Data_mid = 1
        print "state: send med (3)"
    if activity_high and Sent_Data_mid and not Sent_Data_high:
        Sent_Data_high = 1
        print "state: NO send med (still 3)"
    if Sent_Data_high and not activity_high and not Sent_dig_state:
        fb.put('/nest1/','hatchState','4') #"path","property_Name",property_Value
        Sent_dig_state = 1
        print "state: send dig (still 4)"
    if Sent_dig_state and not activity_low and not Sent_end_state:
        fb.put('/nest1/','hatchState','5') #"path","property_Name",property_Value
        Sent_end_state = 1
        print "state: send end (still 5)"

    print np.sum(activity),',',activity_low,',',activity_mid,',',activity_high
    plt.plot(data_diff_mean)

    plt.show()
    fig.canvas.draw()
    fig.canvas.flush_events()

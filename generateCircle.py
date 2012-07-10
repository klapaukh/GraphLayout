#!/usr/local/python

import math

max = 2* math.pi
cx=1920/2
cy=1080/2
width=0.7*min(cx,cy)
height=width
numSteps = 4
for angle in range(0,numSteps,1):
    end = ""
    if(angle == 0):
            end = "," + str(numSteps-1)+ ","
    if(angle < numSteps -1):
        end = end + "," + str(angle+1) + ","
    print ",Select," + str(int(cx + width* math.cos(angle*max/numSteps))) +","+ str(int(cy + height * math.sin(angle*max/numSteps))) + ",1,1" + end   
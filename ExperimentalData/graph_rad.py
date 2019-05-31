import numpy as np
import matplotlib.pyplot as plt
from statistics import mean, median,variance,stdev
from enum import IntEnum, auto

# データの項目
class item(IntEnum):
    time = 0
    acc_x = auto()
    acc_y = auto()
    acc_z = auto()
    vel_x = auto()
    vel_y = auto()
    vel_z = auto()
    pos_x = auto()
    pos_y = auto()
    pos_z = auto()
    rad_vel_x = auto()
    rad_vel_y = auto()
    rad_vel_z = auto()
    rad_gyro_x = auto()
    rad_gyro_y = auto()
    rad_gyro_z = auto()
    rad_mag_x = auto()
    rad_mag_y = auto()
    rad_mag_z = auto()
    pos_step_gyro_x = auto()
    pos_step_gyro_y = auto()
    pos_step_mag_x = auto()
    pos_step_mag_y = auto()
    pos_step_x = auto()
    pos_step_y = auto()

# データの読み込み
filename = "turnTable6"
data = np.loadtxt("data/" + filename + ".txt", delimiter = ",", skiprows = 2, unpack = True) 
fig1 = plt.figure(figsize=(8, 8))

graph = fig1.add_subplot(211) 
# graph.plot(data[time],data[rad_vel_x] * 180/np.pi, color="red", label = "x")
# graph.plot(data[time],data[rad_vel_y] * 180/np.pi, color="blue", label = "y")
graph.plot(data[0], data[item.rad_vel_z]*180/np.pi, color="gray", label = "z")
plt.tick_params(labelbottom=False, bottom=False)
graph.set_ylabel("angular velocity[deg/s]")
# plt.grid(color="gray")
graph.yaxis.set_label_coords(-0.1, 0.5)
# plt.xlim(0,60)
# plt.ylim(-0.13,0.13)
# plt.legend(bbox_to_anchor=(1, 1), loc="upper right", borderaxespad=0, fontsize=12)

graph = fig1.add_subplot(212)
# graph = fig1.add_subplot(111)
graph.plot(data[item.time], data[item.rad_gyro_z]*180/np.pi, color="red", label = "gyro")
graph.plot(data[item.time], data[item.rad_mag_z]*180/np.pi, color="blue", label = "mag")
# graph.plot(data[item.time], (data[item.time]+25)*6.12%360-180, color="gray", label = "expected")
plt.grid(color="gray")
plt.legend(bbox_to_anchor=(1, 1), loc="upper right", borderaxespad=0, fontsize=12)
x = 0
# plt.xlim(x,x+100)
# plt.ylim(80,130)
graph.set_ylabel("z [deg]")
graph.yaxis.set_label_coords(-0.1, 0.5)
graph.set_xlabel("time[s]")
plt.savefig("graph/rad/" + filename + ".png")

# print("標準偏差")
# print("gyro: {0:.2f}".format(stdev(data[item.rad_gyro_z]*180/np.pi)))
# print("mag: {0:.2f}".format(stdev(data[item.rad_mag_z ]*180/np.pi)))

plt.show()
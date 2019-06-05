import numpy as np
import matplotlib.pyplot as plt
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
filename = "turnTable7"
data = np.loadtxt("data/" + filename + ".txt", delimiter = ",", skiprows = 2, unpack = True) 
fig1 = plt.figure(figsize=(8, 8))

# ローパスフィルタ
gyro_z_LPF = [ data[item.rad_vel_z][0] ]
i = 1
C = 0.1
for nowdata in data[item.rad_vel_z]:
    gyro_z_LPF.append( (1-C) * gyro_z_LPF[i-1] + C * nowdata )
    i+=1
gyro_z_LPF.pop(i-1)
gyro_z_LPF_deg = list(map(lambda x: x * 180/np.pi, gyro_z_LPF))

graph = fig1.add_subplot(211) 
# graph.plot(data[time],data[rad_vel_x] * 180/np.pi, color="red", label = "x")
# graph.plot(data[time],data[rad_vel_y] * 180/np.pi, color="blue", label = "y")
graph.plot(data[item.time], data[item.rad_vel_z]*180/np.pi, color="gray", label = "Raw data")
# graph.plot(data[item.time], gyro_z_LPF, color="black", label = "z")
graph.plot(data[item.time], gyro_z_LPF_deg, color="black", label = "LPF")
plt.tick_params(labelbottom=False, bottom=False)
graph.set_ylabel("angular velocity[deg/s]")
graph.yaxis.set_label_coords(-0.1, 0.5)
plt.legend(bbox_to_anchor=(1, 1), loc="upper right", borderaxespad=0, fontsize=12)
# plt.xlim(0,60)
# plt.ylim(-0.13,0.13)

graph = fig1.add_subplot(212)
graph.plot(data[item.time], data[item.rad_gyro_z]*180/np.pi, color="red", label = "gyro")
graph.plot(data[item.time], data[item.rad_mag_z]*180/np.pi, color="blue", label = "mag")
# graph.plot(data[item.time], (data[item.time]+25)*6.12%360-180, color="gray", label = "expected")
plt.grid(color="gray")
plt.legend(bbox_to_anchor=(1, 1), loc="upper right", borderaxespad=0, fontsize=12)
# plt.xlim(x,x+100)
# plt.ylim(80,130)
graph.set_ylabel("z [deg]")
graph.yaxis.set_label_coords(-0.1, 0.5)
graph.set_xlabel("time[s]")
plt.savefig("graph/rad/" + filename + ".png")

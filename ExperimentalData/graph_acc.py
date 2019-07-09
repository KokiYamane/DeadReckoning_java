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

xmin = 0
xmax = xmin + 20
ymin = -10
ymax = 10

# ローパスフィルタ
def LPF(list):
    list_LPF = [ list[0] ]
    i = 1
    C = 0.1
    for nowdata in list:
        list_LPF.append( (1-C) * list_LPF[i-1] + C * nowdata )
        i+=1
    list_LPF.pop(i-1)
    return list_LPF

# データの読み込み
filename = "roundabout1"
data = np.loadtxt("data/" + filename + ".txt", delimiter = ",", skiprows = 2, unpack = True) 

# グラフ作成
fig1 = plt.figure(figsize=(6, 6))

graph = fig1.add_subplot(311) 
graph.plot(data[item.time], data[item.acc_x], color="black")
# graph.plot(data[item.time], LPF(data[item.acc_x]))
plt.grid()
plt.tick_params(labelbottom=False, bottom=False)
graph.set_ylabel("x [m/s^2]")
plt.xlim(xmin,xmax)
plt.ylim(ymin,ymax)

graph = fig1.add_subplot(312)
graph.plot(data[item.time], data[item.acc_y], color="black")
# graph.plot(data[item.time], LPF(data[item.acc_y]))
plt.grid()
plt.tick_params(labelbottom=False, bottom=False)
graph.set_ylabel("y [m/s^2]")
plt.xlim(xmin,xmax)
plt.ylim(ymin,ymax)

graph = fig1.add_subplot(313)
graph.plot(data[item.time], data[item.acc_z], color="black")
# graph.plot(data[item.time], LPF(data[item.acc_z]))
plt.grid()
graph.set_ylabel("z [m/s^2]")
plt.xlim(xmin,xmax)
plt.ylim(ymin,ymax)

graph.set_xlabel("time [s]")

plt.savefig("graph/acc/" + filename + ".png")
plt.show()

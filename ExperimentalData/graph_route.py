import numpy as np
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D
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
    rad_vel_LPF_x = auto()
    rad_vel_LPF_y = auto()
    rad_vel_LPF_z = auto()
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
filename = "notLPF"
data = np.loadtxt("data/" + filename + ".csv", delimiter = ",", skiprows = 1, unpack = True) 

# 2Dグラフ
fig1 = plt.figure(figsize=(6, 6))
graph = fig1.add_subplot(111)
graph.plot(data[item.pos_step_gyro_x],data[item.pos_step_gyro_y], color="red", label="gyro")
graph.plot(data[item.pos_step_mag_x],data[item.pos_step_mag_y], color="blue", label="mag")
# graph.plot(data[item.pos_step_x],data[item.pos_step_y], color="yellow", label="gyro+mag")
# graph.plot(data[item.pos_x],data[item.pos_y], color="blue", label="mag")
graph.set_xlabel("x [m]")
graph.set_ylabel("y [m]")
graph.set_aspect("equal")
# plt.xlim(-20,35)
# plt.ylim(-20,35)
plt.legend(bbox_to_anchor=(0, 1), loc="upper left", borderaxespad=0, fontsize=12)
plt.grid(color="gray")
# plt.legend(bbox_to_anchor=(1, 0), loc="lower right", borderaxespad=0, fontsize=12)
# fig1.patch.set_alpha(0.1) 
plt.savefig("graph/route/" + filename + ".png", transparent=False)

# 3Dグラフ
# fig2 = plt.figure(figsize=(6, 6))
# graph = fig2.add_subplot(111, projection="3d")
# graph.scatter3D(data[pos_x],data[pos_y],data[pos_z])
# graph.set_xlabel("x [m]")
# graph.set_ylabel("y [m]")
# plt.savefig("route3D.png")

# plt.show()

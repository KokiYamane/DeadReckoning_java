import numpy as np
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D

# データの項目
time = 0
acc_x = 1
acc_y = 2
acc_z = 3
vel_x = 4
vel_y = 5
vel_z = 6
pos_x = 7
pos_y = 8
pos_z = 9
rad_vel_x = 10
rad_vel_y = 11
rad_vel_z = 12
rad_gyro_x = 13
rad_gyro_y = 14
rad_gyro_z = 15
rad_mag_x = 16
rad_mag_y = 17
rad_mag_z = 18
pos_step_x = 19
pos_step_y = 20

# データの読み込み
data = np.loadtxt("pos2.txt", delimiter = ",", skiprows = 1, unpack = True)

# 2Dグラフ
fig1 = plt.figure(figsize=(6, 6))
graph = fig1.add_subplot(111)
graph.plot(data[pos_step_x],data[pos_step_y])
# graph.plot(data[pos_x],data[pos_y])
graph.set_xlabel("x [m]")
graph.set_ylabel("y [m]")
graph.set_aspect('equal')
# plt.xlim(-20,35)
# plt.ylim(-20,35)
plt.savefig("route.png")

# 3Dグラフ
# fig2 = plt.figure(figsize=(6, 6))
# graph = fig2.add_subplot(111, projection="3d")
# graph.scatter3D(data[pos_x],data[pos_y],data[pos_z])
# graph.set_xlabel("x [m]")
# graph.set_ylabel("y [m]")
# plt.savefig("route3D.png")

# plt.show()

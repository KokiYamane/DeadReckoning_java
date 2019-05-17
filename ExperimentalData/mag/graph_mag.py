import numpy as np
import matplotlib.pyplot as plt

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

# データの読み込み
data = np.loadtxt("turnTable.txt", delimiter = ',', skiprows = 1, unpack = True)

fig1 = plt.figure(figsize=(10, 5))

# graph = fig1.add_subplot(311)
# graph.plot(data[time],data[rad_gyro_x] * 180/np.pi, color="red", label = "gyro")
# graph.plot(data[time],data[rad_mag_x] * 180/np.pi, color="blue", label = "mag")
# plt.tick_params(labelbottom=False, bottom=False)
# graph.set_ylabel("x [rad]")
# graph.yaxis.set_label_coords(-0.1, 0.5)
# plt.xlim(0,34)
# plt.ylim(-0.13,0.13)

# graph = fig1.add_subplot(312)
# graph.plot(data[time],data[rad_gyro_y] * 180/np.pi, color="red",)
# graph.plot(data[time],data[rad_mag_y] * 180/np.pi, color="blue",)
# plt.tick_params(labelbottom=False, bottom=False)
# graph.set_ylabel("y [rad]")
# graph.yaxis.set_label_coords(-0.1, 0.5)
# plt.xlim(0,34)
# plt.ylim(-0.04,0.07)

graph = fig1.add_subplot(111)
graph.plot(data[time],data[rad_gyro_z] * 180/np.pi, color="red", label = "gyro")
graph.plot(data[time],data[rad_mag_z] * 180/np.pi, color="blue", label = "mag")
plt.legend(bbox_to_anchor=(1, 1), loc='upper right', borderaxespad=0, fontsize=12)
# plt.xlim(0,34)
# plt.ylim(-0.5,1.5)
graph.set_ylabel("z [rad]")
graph.yaxis.set_label_coords(-0.1, 0.5)

graph.set_xlabel("time[s]")

plt.savefig("mag.png")

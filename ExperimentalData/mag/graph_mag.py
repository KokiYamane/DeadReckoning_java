import numpy as np
import matplotlib.pyplot as plt
from statistics import mean, median,variance,stdev

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
data = np.loadtxt("turnTable2.txt", delimiter = ",", skiprows = 1, unpack = True)

fig1 = plt.figure(figsize=(8, 8))

graph = fig1.add_subplot(211)
# graph.plot(data[time],data[rad_vel_x] * 180/np.pi, color="red", label = "x")
# graph.plot(data[time],data[rad_vel_y] * 180/np.pi, color="blue", label = "y")
graph.plot(data[time],data[rad_vel_z] * 180/np.pi, color="gray", label = "z")
plt.tick_params(labelbottom=False, bottom=False)
graph.set_ylabel("angular velocity[deg/s]")
# plt.grid(color="gray")
graph.yaxis.set_label_coords(-0.1, 0.5)
# plt.xlim(2,10)
# plt.ylim(-0.13,0.13)
# plt.legend(bbox_to_anchor=(1, 1), loc="upper right", borderaxespad=0, fontsize=12)

graph = fig1.add_subplot(212)
graph.plot(data[time], data[rad_gyro_z]*180/np.pi, color="red", label = "gyro")
graph.plot(data[time], data[rad_mag_z]*180/np.pi, color="blue", label = "mag")
graph.plot(data[time], (data[time]+25)*6.12%360-180, color="gray", label = "expected")
# plt.grid(color="gray")
plt.legend(bbox_to_anchor=(1, 0), loc="lower right", borderaxespad=0, fontsize=12)
# plt.xlim(510,518)
# plt.ylim(80,130)
graph.set_ylabel("z [deg]")
graph.yaxis.set_label_coords(-0.1, 0.5)
graph.set_xlabel("time[s]")
plt.savefig("turnTable2.png")

print("標準偏差")
print("gyro: {0:.2f}".format(stdev(data[rad_gyro_z]*180/np.pi)))
print("mag: {0:.2f}".format(stdev(data[rad_mag_z ]*180/np.pi)))
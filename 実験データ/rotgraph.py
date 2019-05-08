import numpy as np
import matplotlib.pyplot as plt

time,acc_x,acc_y,acc_z,vel_x,vel_y,vel_z,pos_x,pos_y,pos_z,rotacc_x,rotacc_y,rotacc_z,rotpos_x,rotpos_y,rotpos_z = np.loadtxt("pos.txt", delimiter = ',', skiprows = 1, unpack = True)
fig1 = plt.figure(figsize=(6, 8))

graph = fig1.add_subplot(211)
graph.plot(time,rotacc_x * 180/np.pi, color="red", label = "x")
graph.plot(time,rotacc_y * 180/np.pi, color="blue", label = "y")
graph.plot(time,rotacc_z * 180/np.pi, color="gray", label = "z")
plt.tick_params(labelbottom=False, bottom=False)
graph.set_ylabel("angular velocity[deg/s]")
graph.yaxis.set_label_coords(-0.1, 0.5)
plt.xlim(2,7)
# plt.ylim(-0.13,0.13)
plt.legend(bbox_to_anchor=(1, 1), loc='upper right', borderaxespad=0, fontsize=12)

graph = fig1.add_subplot(212)
graph.plot(time,rotpos_x * 180/np.pi, color="red")
graph.plot(time,rotpos_y * 180/np.pi, color="blue")
graph.plot(time,rotpos_z * 180/np.pi, color="gray")
plt.xlim(2,7)
# plt.ylim(-0.5,1.5)
graph.set_ylabel("rotation[deg]")
graph.yaxis.set_label_coords(-0.1, 0.5)

graph.set_xlabel("time[s]")

plt.savefig("rot.png")

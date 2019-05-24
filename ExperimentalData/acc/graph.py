import numpy as np
import matplotlib.pyplot as plt

for i in range(3):
    time,acc_x,acc_y,acc_z,vel_x,vel_y,vel_z,pos_x,pos_y,pos_z = np.loadtxt(str(i) + ".txt", delimiter = ",", skiprows = 1, unpack = True)
    fig1 = plt.figure(figsize=(6, 8))

    graph = fig1.add_subplot(311)
    graph.plot(time,acc_x, color="red", label = "x")
    graph.plot(time,acc_y, color="blue", label = "y")
    graph.plot(time,acc_z, color="gray", label = "z")
    plt.tick_params(labelbottom=False, bottom=False)
    graph.set_ylabel("acceleration[m/s^2]")
    graph.yaxis.set_label_coords(-0.1, 0.5)
    plt.xlim(0,34)
    # plt.ylim(-0.13,0.13)
    plt.legend(bbox_to_anchor=(1, 1), loc="upper right", borderaxespad=0, fontsize=12)

    graph = fig1.add_subplot(312)
    graph.plot(time,vel_x, color="red")
    graph.plot(time,vel_y, color="blue")
    graph.plot(time,vel_z, color="gray")
    plt.tick_params(labelbottom=False, bottom=False)
    graph.set_ylabel("velocity[m/s]")
    graph.yaxis.set_label_coords(-0.1, 0.5)
    plt.xlim(0,34)
    # plt.ylim(-0.04,0.07)

    graph = fig1.add_subplot(313)
    graph.plot(time,pos_x, color="red")
    graph.plot(time,pos_y, color="blue")
    graph.plot(time,pos_z, color="gray")
    plt.xlim(0,34)
    # plt.ylim(-0.5,1.5)
    graph.set_ylabel("position[m]")
    graph.yaxis.set_label_coords(-0.1, 0.5)

    graph.set_xlabel("time[s]")

    plt.savefig(str(i) + ".png")

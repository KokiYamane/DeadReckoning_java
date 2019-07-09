import numpy as np
import matplotlib.pyplot as plt

for i in range(3):
    time,acc_x,acc_y,acc_z,vel_x,vel_y,vel_z,pos_x,pos_y,pos_z = np.loadtxt(str(i) + ".txt", delimiter = ",", skiprows = 1, unpack = True)
    
    # ローパスフィルタ
    acc_z_LPF = [ acc_z[0] ]
    j = 1
    C = 0.1
    for nowdata in acc_z:
        acc_z_LPF.append( (1-C) * acc_z_LPF[j-1] + C * nowdata )
        j+=1
    acc_z_LPF.pop(i-1)
    # acc_z_LPF_deg = list(map(lambda x: x * 180/np.pi, acc_z_LPF))

    fig1 = plt.figure(figsize=(6, 6))

    graph = fig1.add_subplot(311)
    # graph.plot(time,acc_x, color="red", label = "x")
    # graph.plot(time,acc_y, color="blue", label = "y")
    graph.plot(time-4,acc_z, color="gray", label = "raw data")
    graph.plot(time-4,acc_z_LPF, color="black", label = "LPF")
    plt.tick_params(labelbottom=False, bottom=False)
    graph.set_ylabel("acceleration [m/s^2]")
    graph.yaxis.set_label_coords(-0.1, 0.5)
    plt.xlim(0,30)
    plt.ylim(-0.15,0.15)
    plt.legend(bbox_to_anchor=(0, 1), loc="upper left", borderaxespad=0, fontsize=12)
    plt.grid()

    graph = fig1.add_subplot(312)
    # graph.plot(time,vel_x, color="red")
    # graph.plot(time,vel_y, color="blue")
    graph.plot(time-4,vel_z, color="black")
    plt.tick_params(labelbottom=False, bottom=False)
    graph.set_ylabel("velocity [m/s]")
    graph.yaxis.set_label_coords(-0.1, 0.5)
    # plt.xlim(4,34)
    plt.xlim(0,30)
    plt.ylim(-0.025,0.1)
    plt.grid()

    graph = fig1.add_subplot(313)
    # graph.plot(time,pos_x, color="red")
    # graph.plot(time,pos_y, color="blue")
    graph.plot(time-4,pos_z, color="black")
    # plt.xlim(4,34)
    plt.xlim(0,30)
    plt.ylim(-0.5,1.5)
    graph.set_ylabel("position [m]")
    graph.yaxis.set_label_coords(-0.1, 0.5)
    plt.grid()

    graph.set_xlabel("time [s]")

    plt.savefig(str(i) + ".png")

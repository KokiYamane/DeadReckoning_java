import numpy as np
import matplotlib.pyplot as plt

# 日本語フォント設定
from matplotlib import rcParams
rcParams["font.family"] = "sans-serif"
rcParams["font.sans-serif"] = ["Yu Gothic"]

# データの項目
from enum import IntEnum, auto
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
filename = "stop2"
data = np.loadtxt("data/" + filename + ".txt", delimiter = ",", skiprows = 2, unpack = True) 

# 画面生成
fig = plt.figure(figsize=(6, 6))

# グラフ生成
graph1 = fig.add_subplot(211) 
graph2 = fig.add_subplot(212)

# プロット
graph1.plot(data[item.time]/60, data[item.rad_vel_z]*180/np.pi, color="gray", label = "raw data")
graph1.plot(data[item.time]/60, LPF(data[item.rad_vel_z]*180/np.pi), color="black", label = "LPF")

graph2.plot(data[item.time]/60, data[item.rad_gyro_z]*180/np.pi, color="red", label = "gyro")
graph2.plot(data[item.time]/60, data[item.rad_mag_z]*180/np.pi, color="blue", label = "mag")

# 値の範囲
xmin = 0
xmax = 5
graph1.set_xlim(xmin,xmax)
graph2.set_xlim(xmin,xmax)

ymin = -180
ymax =  180
graph2.set_ylim(ymin,ymax)

# 目盛
graph2.set_yticks(np.linspace(-180, 180, 9))

# ラベル
graph1.tick_params(labelbottom=False, bottom=False)
graph2.set_xlabel("時間 [分]")

graph1.set_ylabel("角速度 [deg/s]")

graph2.set_ylabel("角度 [deg]")

graph1.yaxis.set_label_coords(-0.1, 0.5)
graph2.yaxis.set_label_coords(-0.1, 0.5)

# グリッド
graph1.grid()
graph2.grid()

# 凡例
# graph1.legend(bbox_to_anchor=(0, 0), loc="lower left", borderaxespad=0, fontsize=12)

# 画像出力
fig.savefig("graph/rad/" + filename + ".png")

# plt.show()
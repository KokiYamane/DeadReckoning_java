import numpy as np
import matplotlib.pyplot as plt

data = [0.1]
for i in range(1,100):
    data.append(0.9 * data[i-1])

fig = plt.figure(figsize=(6,6))
graph = fig.add_subplot(211)
graph.plot(data)
plt.xlabel("number")
plt.ylabel("coefficient")

f = open("LPFcoefficient.txt", mode="wt")
for i in data:
    f.write("{:8f}".format(i) + "\n")

# DFT後のデータ
data2 = np.loadtxt("LPFcoefficient_DFT.txt", delimiter = "\t", unpack = True) 
graph2 = fig.add_subplot(212)
f = np.arange(0.0, 50.0, 0.5)
graph2.plot(data2[0])
plt.xlim(0,25)
plt.xlabel("frequency [Hz]")
plt.ylabel("amplitude [dB]")
# plt.grid()
plt.savefig("graph/LPFcoefficient.png")
plt.show()

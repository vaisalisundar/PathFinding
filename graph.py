import pandas as pd
import matplotlib.pyplot as plt

# reading the file
df = pd.read_csv("/Users/Vaisali/PycharmProjects/Learning_Spark/density_vs_shortestpathlength.csv")

# plotting the points
plt.plot(df["p"],df["length"])

# naming the x axis
plt.xlabel("p-value")

# naming the y axis
plt.ylabel("Shortest Path Length")

# giving a title to my graph
plt.title('Density Vs Shortest Path Length')

# function to show the plot
plt.show()

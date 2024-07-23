library("ggplot2")
df <- read.csv("data/freq.csv")

plot <- ggplot(data = df, aes(x = Frequency, y = Magnitude)) +
  geom_bar(stat = "identity") +
  ggtitle("Spettro delle frequenze")

ggsave("plot.png", plot, "png", "data/", 1)
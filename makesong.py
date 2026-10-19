with open("song.txt", "r") as f:
    t = f.read().strip()
out = t.replace("\n", "\\n").replace('"', '\\"')
print(f"{out}")

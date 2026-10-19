with open("team.txt", "r") as f:
    t = f.read().strip()
out = ""
for l in t.splitlines():
    out += f'"{l}",'
print(f"[{out}],")

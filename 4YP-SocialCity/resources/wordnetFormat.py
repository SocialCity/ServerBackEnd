def getLines(filename):
	result = []
	with open(filename) as f:
		content = f.readlines()
		f.close

	for line in content:
		line = line.strip(os.linesep)
		if line != "":
			result.append(line)
	return result


from tkinter import Tk
from tkinter import filedialog
import os.path
import os
import re

Tk().withdraw()
filename = filedialog.askopenfilename()
os.chdir(os.path.dirname(filename))
lines = getLines(filename)

output = []

for line in lines:
    regex = "\[" + "[\w, \-]+" + "%"
    value = re.search(regex, line).group(0)
    value = value.strip()
    value = value.strip('[')
    value = value.strip('%')
    output.append(value)


output_file = open("wordnet-core-words.txt", "w", newline='', encoding = 'utf-8')
for line in output:
    output_file.write(line + "\n")
output_file.close
print("done")

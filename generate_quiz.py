import re
import json

with open(r'd:\ASMARA\prototype\script.js', 'r', encoding='utf-8') as f:
    content = f.read()

# Extract soalList
match = re.search(r'const soalList = (\[.+?\]);', content, re.DOTALL)
if not match:
    print("Could not find soalList")
    exit()

list_str = match.group(1)

# It's a JS object list. It uses single quotes and double quotes, and missing quotes on keys.
# Let's clean it up to parse as JSON.
# Add quotes to keys
list_str = re.sub(r'([{,]\s*)([a-zA-Z0-9_]+)(\s*:)', r'\1"\2"\3', list_str)
# Handle comments
list_str = re.sub(r'//.*', '', list_str)
list_str = re.sub(r'/\*.*?\*/', '', list_str, flags=re.DOTALL)
# Single quotes to double quotes, but be careful with html attributes
# Actually, since it's just to extract strings, let's just use regex directly on the JS object string.

questions = []
# split by {
blocks = re.split(r'\{', match.group(1))[1:]
for block in blocks:
    # extract q
    q_match = re.search(r'q:\s*"(.*?)"', block)
    if not q_match: continue
    q = q_match.group(1)
    
    # extract img
    img_match = re.search(r'img:\s*"(.*?)"', block)
    img = img_match.group(1) if img_match else "null"
    if img != "null": img = f'"{img}"'
    
    # extract opts
    opts_match = re.search(r'opts:\s*\[(.*?)\]', block, re.DOTALL)
    opts_raw = opts_match.group(1)
    opts = re.findall(r'"(.*?)"', opts_raw)
    opts_str = ", ".join([f'"{opt}"' for opt in opts])
    
    # extract ans
    ans_match = re.search(r'ans:\s*(\d+)', block)
    ans = ans_match.group(1) if ans_match else "0"
    
    # extract fb
    fb_match = re.search(r'fb:\s*"(.*?)"', block)
    fb = fb_match.group(1) if fb_match else ""
    fb = re.sub(r'<i.*?></i>\s*', '', fb)
    
    # Fallback if opts < 3
    opt0 = opts[0] if len(opts) > 0 else ""
    opt1 = opts[1] if len(opts) > 1 else ""
    opt2 = opts[2] if len(opts) > 2 else ""
    
    java_obj = f'        soalList.add(new Soal("{q}", new String[]{{"{opt0}", "{opt1}", "{opt2}"}}, {ans}, "{fb}", {img}));'
    questions.append(java_obj)

print("\n".join(questions))
with open(r'd:\ASMARA\soal_java.txt', 'w', encoding='utf-8') as f:
    f.write("\n".join(questions))

print("Done")

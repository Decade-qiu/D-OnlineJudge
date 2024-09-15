import subprocess
import sys

def run_command(command):
    result = subprocess.run(command, shell=True, text=True, capture_output=True)
    return result.stdout, result.stderr

def process_time_log(time_log):
    user_time, system_time, status, memo_size = 0, 0, 0, 0
    with open(time_log, 'r') as f:
        lines = f.readlines()
        for line in lines:
            if "Command terminated by signal 9" in line:
                return 4, 0, 0
            if "User time" in line:
                user_time = float(line.split(":")[1].strip().split(" ")[0])
            if "System time" in line:
                system_time = float(line.split(":")[1].strip().split(" ")[0])
            if "Exit status" in line:
                status = int(line.split(":")[1].strip())
            if "Maximum resident set size" in line:
                memo_size = int(line.split(":")[1].strip())
    return status, user_time+system_time, memo_size

# Volumes mapping: /root/doj/sandbox/code/ -> /usr/src/app/
ROOT_PATH = "/root/doj/sandbox/code/run"
DOCKER_PATH = "/usr/src/app/run"

OUTPUT_FILE = "output.txt"
ERROR_FILE = "error.txt"
TIME_FILE = "time.txt"
CODE_FILE = ""

container_name = ""
compile_cmd = ""
run_cmd = ""

# 获取命令行参数
if len(sys.argv) != 3:
    print("Usage: python3 run.py <language> <source>")
    sys.exit(1)

language = sys.argv[1].strip().lower()
CODE_FILE = sys.argv[2].strip()
OUTPUT_FILE = f"{CODE_FILE.split('.')[0]}_output.txt"
ERROR_FILE = f"{CODE_FILE.split('.')[0]}_error.txt"
TIME_FILE = f"{CODE_FILE.split('.')[0]}_time.txt"

# Set container name and code file based on the language
if language == "python":
    container_name = "python3"
    compile_cmd = ""
    run_cmd = f"python3 {DOCKER_PATH}/{CODE_FILE}"
elif language == "java":
    container_name = "java11"
    compile_cmd = f"javac {DOCKER_PATH}/{CODE_FILE}"
    run_cmd = f"java -cp {DOCKER_PATH} Main"
elif language == "cpp" or language == "c++" or language == "c":
    container_name = "gcc13"
    compile_cmd = f"g++ {DOCKER_PATH}/{CODE_FILE} -o {DOCKER_PATH}/main.out"
    run_cmd = f"{DOCKER_PATH}/main.out"
else:
    raise ValueError("Unsupported programming language")

# Command to execute inside the container
if compile_cmd:
    compile_command = f"""
        docker exec {container_name} bash -c \
        "/usr/bin/time -v -o {DOCKER_PATH}/{TIME_FILE} \
        {compile_cmd}" > {ROOT_PATH}/{OUTPUT_FILE} \
        2> {ROOT_PATH}/{ERROR_FILE}
    """
    run_command(compile_command)

    # compilation error
    stdout, _ = run_command(f"cat {ROOT_PATH}/{ERROR_FILE}")
    if stdout.strip():
        print(2)
        print(stdout.strip())
        exit(0)

# Run the code
run = f"""
    docker exec {container_name} bash -c \
    "/usr/bin/time -v -o {DOCKER_PATH}/{TIME_FILE} \
    {run_cmd}" > {ROOT_PATH}/{OUTPUT_FILE} \
    2> {ROOT_PATH}/{ERROR_FILE}
"""
run_command(run)

# runtime error
stdout, _ = run_command(f"cat {ROOT_PATH}/{ERROR_FILE}")
if stdout.strip():
    print(1)
    print(stdout.strip())
    exit(0)

# Get the time info
info = process_time_log(f"{ROOT_PATH}/{TIME_FILE}")
for i in info:
    print(i, end=' ')
print()
# Display appropriate output
res = f"{ROOT_PATH}/{OUTPUT_FILE}"
stdout, _ = run_command(f"cat {res}")
print(stdout.strip())
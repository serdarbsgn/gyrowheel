import subprocess
file_path = "/storage/emulated/0/Android/data/com.serdarbsgn.gyrowheel/files/inputs"
import vgamepad as vg
import json
gamepad = vg.VX360Gamepad()

def simulate_gamepad(inputs):
    gamepad.left_trigger_float(value_float=inputs["LT"])
    gamepad.right_trigger_float(value_float=inputs["RT"])
    inputs["SP"] = -inputs["SP"]
    simulate_joystick_press(inputs["SP"],inputs["SR"])
    gamepad.update()

def simulate_joystick_press(l_x_analog_value,l_y_analog_value):
    gamepad.left_joystick(x_value= l_x_analog_value, y_value=l_y_analog_value)

def read_file_from_phone(file_path):
    try:
        # Execute adb shell cat command to read the file contents
        result = subprocess.run(["adb", "shell", "cat", file_path], capture_output=True, text=True, check=True)
        file_contents = result.stdout.strip()  # Get the file contents from the stdout
        return file_contents
    except subprocess.CalledProcessError as e:
        print("Connect the device with app set to USB")
        return None


tempContents = {"LT":False,"RT":False,"pitch":0}
while 1:
    contents = read_file_from_phone(file_path)
    if contents:
        tempContents = json.loads(contents)
        simulate_gamepad(tempContents)
    else:
        pass


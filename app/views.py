from . import app,gamepad
from flask import request
@app.route('/',methods = ['POST'])
def gyro_inputs():
    simulate_gamepad(request.json)
    return "",200

def simulate_gamepad(inputs):
    gamepad.left_trigger_float(value_float=int(inputs["buttonAPressed"]))
    gamepad.right_trigger_float(value_float=int(inputs["buttonBPressed"]))
    inputs["pitch"] = -inputs["pitch"]
    simulate_joystick_press(inputs["pitch"])
    gamepad.update()

def simulate_joystick_press(l_x_analog_value):
    gamepad.left_joystick(x_value= l_x_analog_value, y_value=0)
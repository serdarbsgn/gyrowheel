from . import app,gamepad
from flask import request
@app.route('/',methods = ['POST'])
def gyro_inputs():
    simulate_gamepad(request.json)
    return "",200

def simulate_gamepad(inputs):
    gamepad.left_trigger_float(value_float=inputs["LT"])
    gamepad.right_trigger_float(value_float=inputs["RT"])
    inputs["SP"] = -inputs["SP"]
    simulate_joystick_press(inputs["SP"],inputs["SR"])
    gamepad.update()

def simulate_joystick_press(l_x_analog_value,l_y_analog_value):
    gamepad.left_joystick(x_value= l_x_analog_value, y_value=l_y_analog_value)
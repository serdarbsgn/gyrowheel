import vgamepad as vg
import json

gamepad = vg.VX360Gamepad()

import socket
import json


def simulate_gamepad(inputs,full):
    gamepad.left_trigger_float(value_float=inputs["LT"])
    gamepad.right_trigger_float(value_float=inputs["RT"])
    inputs["SP"] = -inputs["SP"]
    simulate_joystick_press(inputs["SP"],inputs["SR"])
    if full:
        gamepad.press_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_LEFT_SHOULDER) if inputs["LB"]==1 else gamepad.release_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_LEFT_SHOULDER)
        gamepad.press_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_LEFT_THUMB) if inputs["L3"]==1 else gamepad.release_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_LEFT_THUMB)

        gamepad.press_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_RIGHT_SHOULDER) if inputs["RB"]==1 else gamepad.release_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_RIGHT_SHOULDER)
        gamepad.press_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_RIGHT_THUMB) if inputs["R3"]==1 else gamepad.release_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_RIGHT_THUMB)

        gamepad.press_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_BACK) if inputs["BC"]==1 else gamepad.release_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_BACK)
        gamepad.press_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_START) if inputs["ST"]==1 else gamepad.release_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_START)

        gamepad.press_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_Y) if inputs["Y"]==1 else gamepad.release_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_Y)
        gamepad.press_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_X) if inputs["X"]==1 else gamepad.release_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_X)
        gamepad.press_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_A) if inputs["A"]==1 else gamepad.release_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_A)
        gamepad.press_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_B) if inputs["B"]==1 else gamepad.release_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_B)

        gamepad.press_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_DPAD_UP) if inputs["AU"]==1 else gamepad.release_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_DPAD_UP)
        gamepad.press_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_DPAD_LEFT) if inputs["AL"]==1 else gamepad.release_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_DPAD_LEFT)
        gamepad.press_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_DPAD_RIGHT) if inputs["AR"]==1 else gamepad.release_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_DPAD_RIGHT)
        gamepad.press_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_DPAD_DOWN) if inputs["AD"]==1 else gamepad.release_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_DPAD_DOWN)

    gamepad.update()

def simulate_joystick_press(l_x_analog_value,l_y_analog_value):
    gamepad.left_joystick(x_value= l_x_analog_value, y_value=l_y_analog_value)

def get_ip():
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    s.settimeout(0)
    try:
        s.connect(('255.255.255.255', 1))
        IP = s.getsockname()[0]
    except Exception:
        IP = '127.0.0.1'
    finally:
        s.close()
    return IP
    

HOST = '0.0.0.0'
PORT = 12345

def start_server(IP):
    input_dict = {"SR":0,"SP":0,"LB":0,"LT":0,"L3":0,"RB":0,"RT":0,"R3":0,"BC":0,"ST":0,"Y":0,"X":0,"B":0,"A":0,"AU":0,"AL":0,"AR":0,"AD":0}
    udp_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    udp_socket.bind((HOST, PORT))
    print(f"UDP server started on {IP}:{PORT}")
    while True:
        data, addr = udp_socket.recvfrom(48)
        try:
            inputs = data.decode().split(",")
            inputs = [int(input) for input in inputs]
            if len(inputs)>5:
                input_dict = {"SR":inputs[0],"SP":inputs[1],"LB":inputs[2],"LT":inputs[3],"L3":inputs[4],"RB":inputs[5],
                              "RT":inputs[6],"R3":inputs[7],"BC":inputs[8],"ST":inputs[9],"Y":inputs[10],"X":inputs[11],
                              "B":inputs[12],"A":inputs[13],"AU":inputs[14],"AL":inputs[15],"AR":inputs[16],"AD":inputs[17]}
                simulate_gamepad(input_dict,True)
            else:
                input_dict = {"SR":inputs[0],"SP":inputs[1],"LT":inputs[2],"RT":inputs[3]}
                simulate_gamepad(input_dict,False)
        except:
            print("Can't convert to json.")
        #print("Received:", input_dict)

if __name__ == "__main__":
    start_server(get_ip())




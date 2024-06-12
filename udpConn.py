import vgamepad as vg
import json
gamepad = vg.VX360Gamepad()

import socket
import json


def simulate_gamepad(inputs):
    gamepad.left_trigger_float(value_float=int(inputs["buttonAPressed"]))
    gamepad.right_trigger_float(value_float=int(inputs["buttonBPressed"]))
    inputs["pitch"] = -inputs["pitch"]
    simulate_joystick_press(inputs["pitch"])
    gamepad.update()

def simulate_joystick_press(l_x_analog_value):
    gamepad.left_joystick(x_value= l_x_analog_value, y_value=0)

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
    udp_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    udp_socket.bind((HOST, PORT))
    print(f"UDP server started on {IP}:{PORT}")
    skip = False
    while True:
        data, addr = udp_socket.recvfrom(128)
        try:
            json_data = json.loads(data)
            simulate_gamepad(json_data)
        except:
            print("Can't convert to json.")
        #print("Received:", data.decode())

if __name__ == "__main__":
    start_server(get_ip())




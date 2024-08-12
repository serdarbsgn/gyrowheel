import vgamepad as vg
gamepad = vg.VX360Gamepad()

import socket
from simulate_gamepad import simulate_gamepad
from simulate_keyboard_mouse import *
from simulate_keyboard_mouse import simulate_km

keyboard = pyk.Controller()
mouse = pym.Controller()
previous_button_state = {
    'left': False,
    'right': False
}

def get_ip():
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    s.settimeout(0)
    try:
        s.connect(('255.255.255.255', 1))
        IP = s.getsockname()[0]
    except Exception:
        IP = '127.0.0.1',
    finally:
        s.close()
    return IP
    
HOST = '0.0.0.0'
PORT = 12345





def start_server(IP):
    udp_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    udp_socket.bind((HOST, PORT))
    print(f"UDP server started on {IP}:{PORT}")
    while True:
        data, addr = udp_socket.recvfrom(64)
        try:
            inputs = data.decode().split(",")
            inputs = [int(input) for input in inputs]
            l_inputs = len(inputs)
            if l_inputs==20:#GamePad Mode
                input_dict = {
                    "SR": inputs[0], "SP": inputs[1], "LB": inputs[2], "LT": inputs[3], "L3": inputs[4], "RB": inputs[5],
                    "RT": inputs[6], "R3": inputs[7], "BC": inputs[8], "ST": inputs[9], "Y": inputs[10], "X": inputs[11],
                    "B": inputs[12], "A": inputs[13], "AU": inputs[14], "AL": inputs[15], "AR": inputs[16], "AD": inputs[17], 
                    "ARX": inputs[18], "ARY": inputs[19]
                }
                simulate_gamepad(gamepad,input_dict, True)
            elif l_inputs == 4:#GyroWheel Mode
                input_dict = {"SR": inputs[0], "SP": inputs[1], "LT": inputs[2], "RT": inputs[3]}
                simulate_gamepad(gamepad,input_dict, False)
        except:
            try:
                inputs = data.decode().split("|")
                if len(inputs)>5:
                    inputs = [inputs[0],inputs[1]+"|"+inputs[2],int(inputs[3]),int(inputs[4]),int(inputs[5])]
                else:
                    inputs = [inputs[0],inputs[1],int(inputs[2]),int(inputs[3]),int(inputs[4])]
                simulate_km(inputs,previous_button_state,keyboard,mouse)
            except:
                pass

if __name__ == "__main__":
    start_server(get_ip())




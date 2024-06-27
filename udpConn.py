import vgamepad as vg
gamepad = vg.VX360Gamepad()
import socket
from simulate_gamepad import simulate_gamepad
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
            print("Can't convert to json.")
        #print("Received:", input_dict)

if __name__ == "__main__":
    start_server(get_ip())




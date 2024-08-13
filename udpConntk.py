import threading
import vgamepad as vg
import socket
from simulate_gamepad import simulate_gamepad
import tkinter as tk
from simulate_keyboard_mouse import simulate_km
from simulate_keyboard_mouse import *

IP = None
root = None
HOST = None
PORT = None
udp_socket = None
running = None
processor_thread = None
gamepad = None
keyboard = None
mouse = None
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
        IP = '127.0.0.1'
    finally:
        s.close()
    return IP



def start_server():
    global running,udp_socket
    running = True
    udp_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    udp_socket.bind((HOST, PORT))
    root.nametowidget("status_label").config(text=f"UDP server started on {IP}:{PORT}")
    tk.Label(root, text="This is network mode, use network mode on your android").pack()
    tk.Label(root, text="You can close this window to close the app").pack()
    processor_thread = threading.Thread(target=processor, daemon=True)
    processor_thread.start()
    root.nametowidget("start_button").destroy()

def processor():
    while running:
        try:
            data, addr = udp_socket.recvfrom(64)
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

def create_gui():
    global root
    if root is None:
        root = tk.Tk()
    if root:
        root.minsize(300, 100)
        root.title("GW Network Control")
    start_button = tk.Button(root, text="Start", command=start_server,name="start_button")
    start_button.pack(pady=10)

    status_label = tk.Label(root, text="Status: Stopped",name="status_label",font=(15))
    status_label.pack(pady=10)



def main(parent_root = None):
    global IP,HOST,PORT,running,gamepad,keyboard,mouse,root
    if parent_root:
        root = parent_root
    gamepad = vg.VX360Gamepad()
    keyboard = pyk.Controller()
    mouse = pym.Controller()
    IP = get_ip()
    HOST = '0.0.0.0'
    PORT = 12345
    running = False
    create_gui()
    if not parent_root:
        root.mainloop()
if __name__ == '__main__':
    main(None)
    



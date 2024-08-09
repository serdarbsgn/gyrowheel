import threading
import vgamepad as vg
import socket
from simulate_gamepad import simulate_gamepad
import tkinter as tk


IP = None
root = None
HOST = None
PORT = None
udp_socket = None
running = None
processor_thread = None
gamepad = None

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
            pass

def create_gui():
    global root
    if not root:
        root = tk.Tk()
        root.minsize(300, 100)
        root.title("GW Network Control")

    start_button = tk.Button(root, text="Start", command=start_server,name="start_button")
    start_button.pack(pady=10)

    status_label = tk.Label(root, text="Status: Stopped",name="status_label",font=(15))
    status_label.pack(pady=10)



def main():
    global IP,HOST,PORT,running,gamepad
    gamepad = vg.VX360Gamepad()
    IP = get_ip()
    HOST = '0.0.0.0'
    PORT = 12345
    running = False
    create_gui()
    root.mainloop()
if __name__ == '__main__':
    main()
    



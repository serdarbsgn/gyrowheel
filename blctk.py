import tkinter as tk
import threading
import queue
import socket
import subprocess
import re
import vgamepad as vg
from simulate_gamepad import simulate_gamepad
from simulate_keyboard_mouse import simulate_km
from simulate_keyboard_mouse import *

running = None
adapter_addr = None
port = None
buf_size = None
input_queue = None
gamepad = None
root = None
keyboard = None
mouse = None
previous_button_state = {
    'left': False,
    'right': False
}
def setup_bluetooth_adapter():
    result = subprocess.run(["ipconfig", "/all"], capture_output=True)
    result_string = result.stdout.decode(errors="ignore")
    result_to_bt = result_string.find("Bluetooth")
    global adapter_addr
    adapter_addr = 'e8:48:b8:c8:20:00'
    
    if result_to_bt != -1:
        found_mac_adresses = re.findall(r"\b[A-F0-9]{2}(?:-[A-F0-9]{2}){5}\b", result_string[result_to_bt:-1])
        for i in range(len(found_mac_adresses)):
            found_mac_adresses[i] = found_mac_adresses[i].replace("-", ":")
        found_mac_adresses_str = '\n'.join(found_mac_adresses)
        root.nametowidget("status_label").config(text=f"Probable addresses:\n{found_mac_adresses_str}")
        if len(found_mac_adresses) == 1:
            root.nametowidget("status_label").config(text=f"Only found one address, will use this:\n {found_mac_adresses_str}")
            adapter_addr = found_mac_adresses[0]
        else:
            def select_address():
                global adapter_addr
                selected = address_entry.get()
                if selected.isdigit():
                    try:
                        adapter_addr = found_mac_adresses[int(selected) - 1]
                        address_entry.destroy()
                        address_label.destroy()
                        address_button.destroy()
                        root.nametowidget("status_label").config(text="Adapter is set.")
                    except IndexError:
                        root.nametowidget("status_label").config(text="Invalid selection.")
                else:
                    root.nametowidget("status_label").config(text="Invalid selection.")
    
            address_label = tk.Label(root, text="Select address number: 1/2/3 etc.")
            address_label.pack(pady=10)
            address_entry = tk.Entry(root)
            address_entry.pack(pady=5)
            address_button = tk.Button(root, text="Confirm", command=select_address)
            address_button.pack(pady=15)
    else:
        def enter_address():
            global adapter_addr
            inp = address_entry.get().upper()
            try:
                adapter_addr = re.findall(r"[A-F0-9]{2}(?::[A-F0-9]{2}){5}", inp)[0]
                address_entry.destroy()
                address_label.destroy()
                address_button.destroy()
                root.nametowidget("status_label").config(text="Configuration complete")
            except Exception as e:
                root.nametowidget("status_label").config(text="Enter a valid address.")
        address_label = tk.Label(root, text="Enter address manually:")
        address_label.pack(pady=10)
        address_entry = tk.Entry(root)
        address_entry.pack(pady=5)
        address_button = tk.Button(root, text="Confirm", command=enter_address)
        address_button.pack(pady=15)
    root.nametowidget("setup_button").destroy()
    
def serial_listener():
    root.nametowidget("status_label").config(text=f'Listener started,waiting connection.')
    tk.Label(root, text="This is bluetooth mode, use bluetooth mode on your android").pack()
    tk.Label(root, text="You can close this window to close the app").pack()
    while running:
        s = socket.socket(socket.AF_BLUETOOTH, socket.SOCK_STREAM, socket.BTPROTO_RFCOMM)
        s.bind((adapter_addr, port))
        s.listen(1)
        try:
            client, address = s.accept()
            root.nametowidget("status_label").config(text=f'Connected to {address}')
            while running:
                data = client.recv(buf_size)
                if data:
                    try:
                        inputs = data.decode().split(",")
                        inputs = [int(input) for input in inputs]
                        l_inputs = len(inputs)
                        if l_inputs == 20:  # GamePad Mode
                            input_dict = {
                                "SR": inputs[0], "SP": inputs[1], "LB": inputs[2], "LT": inputs[3], "L3": inputs[4], "RB": inputs[5],
                                "RT": inputs[6], "R3": inputs[7], "BC": inputs[8], "ST": inputs[9], "Y": inputs[10], "X": inputs[11],
                                "B": inputs[12], "A": inputs[13], "AU": inputs[14], "AL": inputs[15], "AR": inputs[16], "AD": inputs[17],
                                "ARX": inputs[18], "ARY": inputs[19]
                            }
                            input_queue.put((input_dict, True))
                        elif l_inputs == 4:  # GyroWheel Mode
                            input_dict = {"SR": inputs[0], "SP": inputs[1], "LT": inputs[2], "RT": inputs[3]}
                            input_queue.put((input_dict, False))
                    except:
                        try:
                            inputs = data.decode().split("|")
                            if len(inputs)>5:
                                inputs = [inputs[0],inputs[1]+"|"+inputs[2],int(inputs[3]),int(inputs[4]),int(inputs[5])]
                            else:
                                inputs = [inputs[0],inputs[1],int(inputs[2]),int(inputs[3]),int(inputs[4])]
                            simulate_km(inputs,previous_button_state,keyboard,mouse)
                        except Exception as e:
                            print(e)
                else:
                    waitfor -= 1
                    if waitfor < 0:
                        waitfor = 100
                        break
        except Exception as e:
            root.nametowidget("status_label").config(text=f'Something went wrong: {e}')
        finally:
            client.close()
            s.close()
            root.nametowidget("status_label").config(text='Connection lost, waiting for a new connection...')

def processor():
    while running:
        try:
            input_data = input_queue.get()
            if input_data:
                input_dict, is_gamepad_mode = input_data
                simulate_gamepad(gamepad, input_dict, is_gamepad_mode)
            input_queue.task_done()
        except queue.Empty:
            pass

def start_script():
    global running
    running = True
    root.nametowidget("status_label").config(text="Starting...")
    listener_thread = threading.Thread(target=serial_listener, daemon=True)
    processor_thread = threading.Thread(target=processor, daemon=True)
    listener_thread.start()
    processor_thread.start()
    root.nametowidget("start_button").destroy()

# Tkinter GUI
def create_gui():
    global root

    root = tk.Tk()
    root.minsize(300, 100)
    root.title("GW Bluetooth Control")

    setup_button = tk.Button(root, text="Setup Bluetooth Adapter", command=setup_bluetooth_adapter,name="setup_button")
    setup_button.pack(pady=10)

    start_button = tk.Button(root, text="Start", command=start_script,name="start_button")
    start_button.pack(pady=10)

    status_label = tk.Label(root, text="Status: Stopped",name="status_label",font=(15))
    status_label.pack(pady=10)

    root.mainloop()

def main():
    global running,adapter_addr,port,input_queue,gamepad,buf_size,keyboard,mouse
    gamepad = vg.VX360Gamepad()
    keyboard = pyk.Controller()
    mouse = pym.Controller()
    running = False
    adapter_addr = ""
    port = 10
    buf_size = 64
    input_queue = queue.Queue(maxsize=2)
    create_gui()
    
if __name__ == '__main__':
    main()

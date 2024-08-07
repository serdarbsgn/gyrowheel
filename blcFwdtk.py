import serial
import threading
import queue
import vgamepad as vg
from simulate_gamepad import simulate_gamepad
import tkinter as tk
gamepad = vg.VX360Gamepad()

# Serial port configuration
running = False
baud_rate = 115200
com_port = 'COM10'
input_queue = queue.Queue(maxsize=2)
listener_thread = None
processor_thread = None
root = None
def serial_listener():
    global listener_thread,processor_thread,com_port,running
    while running:
        try:
            ser = serial.Serial(com_port, baud_rate)
            root.nametowidget("status_label").config(text=f"Listening on {com_port}")
            while running:
                if ser.in_waiting > 0:
                    data = ser.read(ser.in_waiting)
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
                                input_queue.put((input_dict, True))  # True indicates GamePad mode
                            elif l_inputs == 4:  # GyroWheel Mode
                                input_dict = {"SR": inputs[0], "SP": inputs[1], "LT": inputs[2], "RT": inputs[3]}
                                input_queue.put((input_dict, False))  # False indicates GyroWheel mode
                        except Exception as e:
                            print(f"Error processing data: {data}")
        except serial.SerialException as e:
            if 'FileNotFoundError' in str(e) or 'could not open port' in str(e):
                stop_method()
            else:
                root.nametowidget("status_label").config(f"Serial port error: {e}")
                stop_method()
        except Exception as e:
            root.nametowidget("status_label").config(text=e)
            stop_method()

def stop_method():
    global running,com_port,listener_thread,processor_thread
    running = False
    com_port = 'COM10'
    create_gui(True)

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
    global running,com_port,listener_thread,processor_thread
    running = True
    user_input = root.nametowidget("address_entry").get()
    if user_input.upper().strip() != "":
        try:
            user_input = int(user_input)
            com_port = 'COM'+str(user_input)
        except:
            com_port = user_input.upper()
    root.nametowidget("address_entry_explanation").destroy()
    root.nametowidget("address_entry").destroy()
    root.nametowidget("status_label").config(text="Starting...")
    stop_button = tk.Button(root, text="Stop", command=stop_method,name="stop_button")
    stop_button.pack(pady=10)
    listener_thread = threading.Thread(target=serial_listener, daemon=True)
    processor_thread = threading.Thread(target=processor, daemon=True)
    listener_thread.start()
    processor_thread.start()
    root.nametowidget("start_button").destroy()
# Tkinter GUI
def create_gui(fail=False):
    global root
    if not root:
        root = tk.Tk()
        root.minsize(300, 100)
        root.title("GW Bluetooth Port Forward Control")

    address_label = tk.Label(root, text="Enter the COM port (leave blank to default to 'COM10'):",name="address_entry_explanation")
    address_label.pack(pady=10)
    address_entry = tk.Entry(root,name="address_entry")
    address_entry.pack(pady=5)

    start_button = tk.Button(root, text="Start", command=start_script,name="start_button")
    start_button.pack(pady=10)

    status_label = tk.Label(root, text="Status: Stopped",name="status_label",font=(15))
    status_label.pack(pady=10)
    if fail:
        root.nametowidget("stop_button").destroy()
        root.nametowidget("status_label").config(text="The specified COM port may not exist...")


if __name__ == '__main__':
    create_gui()
    root.mainloop()

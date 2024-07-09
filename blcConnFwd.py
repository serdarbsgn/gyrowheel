import serial
import threading
import queue
import vgamepad as vg
from simulate_gamepad import simulate_gamepad 

gamepad = vg.VX360Gamepad()

# Serial port configuration

baud_rate = 115200

com_port = 'COM10'
user_input = input("Enter the COM port (press Enter to default to 'COM10'): ")
if user_input.strip() != "":
    com_port = user_input
input_queue = queue.Queue(maxsize=2)

def serial_listener():
    while True:
        try:
            ser = serial.Serial(com_port, baud_rate)
            print(f"Listening on {com_port}")

            while True:
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
            print(f"Serial port error: {e}")

        except KeyboardInterrupt:
            ser.close()
            print("Connection closed.")
            break

def processor():
    while True:
        try:
            input_data = input_queue.get()
            if input_data:
                input_dict, is_gamepad_mode = input_data
                simulate_gamepad(gamepad, input_dict, is_gamepad_mode)
            input_queue.task_done()
        except queue.Empty:
            pass

listener_thread = threading.Thread(target=serial_listener, daemon=True)
processor_thread = threading.Thread(target=processor, daemon=True)
listener_thread.start()
processor_thread.start()

listener_thread.join()
processor_thread.join()

import socket
import vgamepad as vg
gamepad = vg.VX360Gamepad()
import socket
import subprocess
import re
result = subprocess.run(["ipconfig","/all"],capture_output=True)
result_string = result.stdout.decode(errors="ignore")
result_to_bt = result_string.find("Bluetooth")
adapter_addr = 'e8:48:b8:c8:20:00'
if result_to_bt != -1:
    found_mac_adresses = re.findall(r"\b[A-F0-9]{2}(?:-[A-F0-9]{2}){5}\b",result_string[result_to_bt:-1])
    for i in range(len(found_mac_adresses)):
        found_mac_adresses[i] = found_mac_adresses[i].replace("-",":")
    found_mac_adresses_str = '\n'.join(found_mac_adresses)
    print(f"Probable adresses:\n {found_mac_adresses_str}")
    if len(found_mac_adresses) == 1:
        print("Only found one address, will try to use it.")
        adapter_addr = found_mac_adresses[0]
    else:
        while True:
            inp = input("Identify the address you want to use, 1 for first, etc : ")
            try:
                adapter_addr = found_mac_adresses[int(inp)-1]
                break
            except Exception:
                print("Enter a valid number.")
else:
    while True:
        inp = input("No addresses found, if you're sure you have bluetooth enabled, enter its address manually with : between characters\n")
        try:
            inp = inp.upper()
            adapter_addr = re.findall(r"[A-F0-9]{2}(?::[A-F0-9]{2}){5}", inp)[0]
            break
        except Exception:
            print("Enter a valid address.")
from simulate_gamepad import simulate_gamepad
port = 10
buf_size = 64
waitfor = 100
while True:
    s = socket.socket(socket.AF_BLUETOOTH, socket.SOCK_STREAM, socket.BTPROTO_RFCOMM)
    s.bind((adapter_addr, port))
    s.listen(1)
    try:
        print('Listening for connection...')
        client, address = s.accept()
        print(f'Connected to {address}')

        while True:
            data = client.recv(buf_size)
            if data:
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
                except Exception as e:
                    pass
                    #print(f"Error processing input: {e}")
            else:
                #Trying to connect from phone while already connected causes to get empty data
                #We can use this to unbind and wait for another connection
                #Since phone doesn't know they're connected and want to establish new connection.
                waitfor -=1
                if waitfor<0:
                    waitfor=100
                    break
    except Exception as e:
        print(f'Something went wrong: {e}')
    finally:
        client.close()
        s.close()
        print('Connection lost, waiting for a new connection...')
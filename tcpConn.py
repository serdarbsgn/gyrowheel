import subprocess
import vgamepad as vg
import socket
# Initialize the gamepad
gamepad = vg.VX360Gamepad()

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

def check_adb_devices():
    result = subprocess.run(["adb", "devices"], capture_output=True, text=True)
    devices = result.stdout.strip().split('\n')[1:]  # Skip the first line
    return [line.split()[0] for line in devices if line.strip()]

def setup_adb_reverse():
    devices = check_adb_devices()
    if devices:
        subprocess.run(["adb", "reverse", "tcp:12346", "tcp:12346"])
        print(f"Set up reverse port forwarding for devices")
    else:
        print("No devices connected. Please connect a device and try again.")
        exit(1)
        
HOST = '0.0.0.0'
PORT = 12346

def start_server(IP):
    tcp_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    tcp_socket.setsockopt(socket.IPPROTO_TCP, socket.TCP_NODELAY, True)
    tcp_socket.setsockopt(socket.IPPROTO_TCP, socket.TCP_FASTOPEN, True)
    tcp_socket.bind((HOST, PORT))
    tcp_socket.listen(1)
    print(f"TCP server started on {IP}:{PORT}")

    while True:
        conn, addr = tcp_socket.accept()
        with conn:
            print('Connected by', addr)
            while True:
                data = conn.recv(48)
                if not data:
                    break
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
                    print(e)

if __name__ == "__main__":
    setup_adb_reverse()
    start_server(get_ip())

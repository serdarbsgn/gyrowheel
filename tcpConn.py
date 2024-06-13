import subprocess
import vgamepad as vg
import json
import socket

# Initialize the gamepad
gamepad = vg.VX360Gamepad()

def simulate_gamepad(inputs):
    gamepad.left_trigger_float(value_float=int(inputs["LT"]))
    gamepad.right_trigger_float(value_float=int(inputs["RT"]))
    inputs["pitch"] = -inputs["pitch"]
    simulate_joystick_press(inputs["pitch"])
    gamepad.update()

def simulate_joystick_press(l_x_analog_value):
    gamepad.left_joystick(x_value=l_x_analog_value, y_value=0)

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
                    json_data = json.loads(data)
                    simulate_gamepad(json_data)
                except json.JSONDecodeError:
                    print("Can't convert to JSON.")
                #print("Received:", data.decode())

if __name__ == "__main__":
    setup_adb_reverse()
    start_server(get_ip())

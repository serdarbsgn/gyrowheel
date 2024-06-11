import usb.core
import usb.util
import time

VENDOR_ID = 0x2717  # Replace with your device's vendor ID
PRODUCT_ID = 0xff40  # Replace with your device's product ID
USB_TIMEOUT_IN_MS = 10000  # 10 seconds timeout

# Initialize the USB device
dev = usb.core.find(idVendor=VENDOR_ID, idProduct=PRODUCT_ID)
if dev is None:
    raise ValueError("Device not found")

# Detach any kernel driver if attached (necessary on some systems)
if dev.is_kernel_driver_active(0):
    dev.detach_kernel_driver(0)

# Set the active configuration. With no arguments, the first configuration will be the active one
dev.set_configuration()

# Get the active configuration
cfg = dev.get_active_configuration()
intf = cfg[(0, 0)]

# Find the endpoints
endpoint_out = None
endpoint_in = None

for ep in intf:
    if usb.util.endpoint_direction(ep.bEndpointAddress) == usb.util.ENDPOINT_OUT:
        endpoint_out = ep
    elif usb.util.endpoint_direction(ep.bEndpointAddress) == usb.util.ENDPOINT_IN:
        endpoint_in = ep

if endpoint_out is None or endpoint_in is None:
    raise ValueError("Endpoints not found")

# Helper function to send control transfer
def init_string_control_transfer(device, index, string):
    device.ctrl_transfer(0x40, 52, 0, index, string.encode('utf-8'), len(string), USB_TIMEOUT_IN_MS)

# Initialize the accessory strings
init_string_control_transfer(dev, 0, "quandoo")  # MANUFACTURER
init_string_control_transfer(dev, 1, "Android2AndroidAccessory")  # MODEL
init_string_control_transfer(dev, 2, "showcasing android2android USB communication")  # DESCRIPTION
init_string_control_transfer(dev, 3, "0.1")  # VERSION
init_string_control_transfer(dev, 4, "http://quandoo.de")  # URI
init_string_control_transfer(dev, 5, "42")  # SERIAL

# Start the accessory mode
dev.ctrl_transfer(0x40, 53, 0, 0, [], 0, USB_TIMEOUT_IN_MS)

# Example: Send data to the accessory
data_to_send = b'Hello from Windows Python'
try:
    dev.write(endpoint_out.bEndpointAddress, data_to_send, timeout=USB_TIMEOUT_IN_MS)
    print("Data sent")
except usb.core.USBError as e:
    print(f"Error sending data: {e}")

# Example: Read data from the accessory
try:
    while True:
        data = dev.read(endpoint_in.bEndpointAddress, endpoint_in.wMaxPacketSize, timeout=USB_TIMEOUT_IN_MS)
        print("Received:", data)
        time.sleep(1)  # Adjust sleep time as needed
except usb.core.USBError as e:
    print(f"Error reading data: {e}")
finally:
    # Release the interface
    usb.util.release_interface(dev, intf)
    dev.reset()

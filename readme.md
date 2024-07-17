Needs a computer that has python3 installed, [Install python here](https://www.python.org/downloads/) then you can use install.bat to install necessary libraries.  
Install apk to your phone, then connect your computer and phone to the same network.(Not necessary for Bluetooth mode.)    

![alt text](readme-photos/run-bat.png "run-bat")  
Use run.bat to select run options    

![alt text](/readme-photos/run-udp-mode.png "udp-mode")  
To use Network/UDP mode, write the IP that terminal displays to your phone.,   
Above example is showing 192.168.1.42:12345   
![alt text](/readme-photos/udp-mode-enter-ip.jpg "udp-mode-enter-ip-on-phone")  
Then you should select the "USE NETWORK" option and   
You should write 192.168.1.42 to first text field   
After correctly entering the ip displayed on your pc to the field   
Use GyroWheel Mode or Gamepad Mode  
When using Gamepad mode, you can edit the layout of the buttons using EDIT LAYOUT BUTTON, dragging and dropping them or pinching them to make them scale.  
Then switch the Use Custom Layout switch on before clicking Gamepad Mode button to use your custom layout.   


To use bluetooth mode, it requires user to give permissions for bluetooth and a bluetooth enabled computer(obviously).    
It requires to know the MAC adress of your PC(You can use SHOW BLUETOOTH COMPUTERS button to get MAC addresses of computers nearby),  
and you can use built in tools to scan for computers around and connect to yours(use CONNECT TO MAC ADDRESS button to initiate connection), while script is active on your computer.    
When successful connection is established, GYROWHEEL MODE and GAMEPAD MODE buttons will light up for you to use.   

Switch labelled as Forwarded socket is to connect using android's built in high level connect function, in windows, search for "Other Bluetooth Options" and navigate to,     
COM tab, and add an Incoming COM port, and enter the value of the COM port that windows assigns,(My computer defaults to COM10, so if left blank script looks for COM10)  
Then follow the same steps above, selecting the Start Bluetooth Classic app with Forwarded port on your computer.   

Now your computer should be able to get inputs and simulate a controller in your games,  


At the bottom of the screen, there's an edit layout button to let you drag and drop buttons to wherever you please even outside if you don't want to use those,   
Then use the switch next to the edit button to use your layout of buttons in gamepad mode.   

There was multiple other methods of connection that i removed because they were fiddly and slow, good learning experience though.  

Currently working on Bluetooth Low Energy mode. I couldn't manage it, connection attempt between Windows and Android BLE GATT server fails and i couldn't fix it.   
# GyroWheel 

<Virtual Gamepad for Windows using an Android device>

## License  

This project is licensed under the GNU General Public License version 3 (GPLv3). You can redistribute it and/or modify it under the terms of the GNU GPLv3.See the [LICENSE](LICENSE) file for details.  

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.  

You should have received a copy of the GNU General Public License along with this program. If not, see [https://www.gnu.org/licenses/](https://www.gnu.org/licenses/).  

## Third-Party Licenses  

This project uses the following libraries:  

- **vgamepad**: Licensed under the MIT License. See [LICENSES/vgamepad-LICENSE](LICENSES/vgamepad-LICENSE).  
- **pyserial**: Licensed under the BSD License. See [LICENSES/pyserial-LICENSE](LICENSES/pyserial-LICENSE).  

The full license texts for these libraries are included in the `LICENSES` directory of this repository.  

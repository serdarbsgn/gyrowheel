Needs a computer that has python3 installed, then you can use install.bat to install necessary libraries.  
Install apk to your phone, then connect your computer and phone to the same network.(Not necessary for Bluetooth mode.)    

Use run.bat to select run options    

Best performance should be in Network/UDP mode, write the IP that terminal displays,  
  
UDP server started on 192.168.1.36:12345  
Then you should write 192.168.1.36 to first text field    


The second best option should be the bluetooth mode, it requires user to give permissions for bluetooth and a bluetooth enabled computer(obviously).    
It requires to know the MAC adress of your PC, and you can use built in tools to scan for computers around and connect to yours, while script is active on your computer.    

Switch labelled as Forwarded socket is to connect using android's built in high level connect function, in windows, search for "Other Bluetooth Options" and navigate to,     
COM tab, and add an Incoming COM port, and enter the value of the COM port that windows assigns,(My computer defaults to COM10, so if left blank script looks for COM10)  

, if you choose flask the server to listen for phone, and note the ip address that is not 127.0.0.1  
Lets assume in terminal you see this,  
 * Serving Flask app 'app/webapp'  
 * Debug mode: off  
WARNING: This is a development server. Do not use it in a production deployment. Use a production WSGI server instead.  
 * Running on all addresses (0.0.0.0)  
 * Running on http://127.0.0.1:5000  
 * Running on http://192.168.1.36:5000  

Then you should write http://192.168.1.36:5000 to the text box inside the phone app, then touch confirm.  
Now your computer should be able to get inputs and simulate a controller in your games,  


If you want to use USB mode, you must enable USB Debugging,    
And also adb drivers since python script relies on it.  
There's another option to reverse proxy your tcp connection on your phone so flask app will work on ADB as well,    

This is a little fiddly since you need to use this address http://127.0.0.1:5000    
and use adb command "adb reverse tcp:5000 tcp:5000" effectively mirroring the request that app to phone over USB cable.    

Third option is to skip flask and use pure reverse TCP connection over ADB, to achieve this, enter 127.0.0.1 to first text field,   
then run the third option in run.bat file.  

I think you wouldn't use this in any other game than a racing game, so keep in mind that.   
  
I'm thinking of improving this further, to fully emulate a controller at some point.   
It has all the buttons of an XBOX 360 controller now.      
Apk should run on devices that are Android 8 or above.   

Currently working on Bluetooth Low Energy mode. This I couldn't manage, connection attempt between Windows and Android BLE GATT server fails and i couldn't fix it.   

At the bottom of the screen, there's an edit layout button to let you drag and drop buttons to wherever you please even outside if you don't want to use those,   
Then use the switch next to the edit button to use your layout of buttons in gamepad mode.   
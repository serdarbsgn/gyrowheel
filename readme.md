Needs a computer that has python3 installed, then you can use install.bat to install necessary libraries.  
Install apk to your phone, then connect your computer and phone to the same network.  
Use run.bat to select run options, if you choose flask the server to listen for phone, and note the ip address that is not 127.0.0.1  
Lets assume in terminal you see this,  
 * Serving Flask app 'app/webapp'  
 * Debug mode: off  
WARNING: This is a development server. Do not use it in a production deployment. Use a production WSGI server instead.  
 * Running on all addresses (0.0.0.0)  
 * Running on http://127.0.0.1:5000  
 * Running on http://192.168.1.36:5000  

Then you should write http://192.168.1.36:5000 to the text box inside the phone app, then touch confirm.  
Now your computer should be able to get inputs and simulate a controller in your games,  

Best performance should be in Network/UDP mode, write the IP that terminal displays,  
  
UDP server started on 192.168.1.36:12345  
  
Then you should write 192.168.1.36 to second text field  
If you want to use USB mode, you must enable USB Debugging, 
And also adb drivers since python script relies on it.  
There's another option to reverse proxy your tcp connection on your phone so flask app will work on ADB as well,  

This is a little fiddly since you need to use this address http://127.0.0.1:5000  
and use adb command "adb reverse tcp:5000 tcp:5000" effectively mirroring the request that app to phone over USB cable.  

Third option is to skip flask and use pure reverse TCP connection over ADB, to achieve this, enter 127.0.0.1 to first text field,  
then run the third option in run.bat file.  

I think you wouldn't use this in any other game than a racing game, so keep in mind that.  
  
I'm thinking of improving this further, to fully emulate a controller at some point.  
Needs a computer that has python3 installed, then you can use install.bat to install necessary libraries.  
Install apk to your phone, then connect your computer and phone to the same network.  
Use run.bat to run the server to listen for phone, and note the ip address that is not 127.0.0.1  
Lets assume in terminal you see this,  
 * Serving Flask app 'app/webapp'  
 * Debug mode: off  
WARNING: This is a development server. Do not use it in a production deployment. Use a production WSGI server instead.  
 * Running on all addresses (0.0.0.0)  
 * Running on http://127.0.0.1:5000  
 * Running on http://192.168.1.36:5000  

Then you should write http://192.168.1.36:5000 to the text box inside the phone app, then touch confirm.  
Now your computer should be able to get inputs and simulate a controller in your games,  
I think you wouldn't use this in any other game than a racing game, so keep in mind that.
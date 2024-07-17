@echo off
cls
echo Choose the method to start the app:
echo 1. Start Bluetooth Classic app
echo 2. Start Bluetooth Classic app with Forwarded port
echo 3. Just press enter to start Network/UDP app
set /p choice="Enter your choice (1/2/anything): "
call venv\Scripts\activate

if "%choice%"=="1" (
    echo Start Bluetooth app
    python blcConn.py 
)else if "%choice%"=="2" (
    echo Start Bluetooth app with Forwarded socket switch on
    python blcConnFwd.py
)else (
	echo Start Network/UDP app
    python udpConn.py
)

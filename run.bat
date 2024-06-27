@echo off
cls
echo Choose the method to start the app:
echo 1. Start Network(Flask) app
echo 2. Start USB app
echo 3. Start ABD/TCP reverse proxy app
echo 4. Start Bluetooth Classic app
echo 5. Just press enter to start Network/UDP app

set /p choice="Enter your choice (1/2/3/4/anything): "
call venv\Scripts\activate

if "%choice%"=="1" (
	echo Start Network/TCP ADB flask app
    flask --app app\webapp run --host 0.0.0.0
)else if "%choice%"=="2" (
    echo Start USB app
    python usbConn.py 
)else if "%choice%"=="3" (
    echo Start ABD/TCP reverse proxy app
    python tcpConn.py 
)else if "%choice%"=="4" (
    echo Start Bluetooth app
    python blcConn.py 
)else (
	echo Start Network/UDP app
    python udpConn.py
)

@echo off
cls
echo Choose the method to start the app:
echo 1. Start Network(Flask) app
echo 2. Start USB app
echo 3. Just press enter to start USB/UDP app

set /p choice="Enter your choice (1/2/''): "
call venv\Scripts\activate

if "%choice%"=="1" (
	echo Start Network app
    flask --app app\webapp run --host 0.0.0.0
)else if "%choice%"=="2" (
    echo Start USB app
    python usbConn.py 
)else (
	echo Start USB/UDP app
    python udpConn.py
)

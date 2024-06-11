@echo off
cls
echo Choose the method to start the app:
echo 1. Start Network(Flask) app
echo 2. Start USB app

set /p choice="Enter your choice (1/2): "

if "%choice%"=="1" (
	echo Start Network app
    call venv\Scripts\activate
    flask --app app\webapp run --host 0.0.0.0
) else (
	echo Start USB app
    call venv\Scripts\activate
    python usbConn.py
)

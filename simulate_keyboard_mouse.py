import pynput.keyboard as pyk
import pynput.mouse as pym
def simulate_km(data,previous_button_state,keyboard,mouse):
    if data[0]:
        if data[0] == "space":
            keyboard.press(pyk.Key.space) 
        elif data[0] == "backspace":
            keyboard.press(pyk.Key.backspace) 
        elif data[0] == "enter":
            keyboard.press(pyk.Key.enter) 
    if data[1]:
        keyboard.type(data[1])
    if data[2] != 0 and data[3]!= 0:
        mouse.move(data[2],data[3])

    status = int(data[4])

    right_pressed = bool(status & 2)
    if right_pressed != previous_button_state['right']:
        if right_pressed:
            mouse.press(pym.Button.right)
        else:
            mouse.release(pym.Button.right)
        previous_button_state['right'] = right_pressed

    left_pressed = bool(status & 1)
    if left_pressed != previous_button_state['left']:
        if left_pressed:
            mouse.press(pym.Button.left)
        else:
            mouse.release(pym.Button.left)
        previous_button_state['left'] = left_pressed
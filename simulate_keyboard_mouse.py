import pynput.keyboard as pyk
import pynput.mouse as pym
def simulate_km(data,previous_button_state,keyboard,mouse):
    def press_release(keycode):
        keyboard.press(keycode)
        keyboard.release(keycode)
    if data[0]:
        if data[0] == "space":
            press_release(pyk.Key.space)
        elif data[0] == "backspace":
            press_release(pyk.Key.backspace) 
        elif data[0] == "enter":
            press_release(pyk.Key.enter)
        elif data[0] == "m_next":
            press_release(pyk.Key.media_next),
        elif data[0] == "m_previous":
            press_release(pyk.Key.media_previous)
        elif data[0] == "m_play":
            press_release(pyk.Key.media_play_pause)
        elif data[0] == "m_vol_down":
            press_release(pyk.Key.media_volume_down)
        elif data[0] == "m_vol_up":
            press_release(pyk.Key.media_volume_up)
        elif data[0] == "m_vol_mute":
            press_release(pyk.Key.media_volume_mute)
        elif data[0] == "windows":
            press_release(pyk.Key.cmd)
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
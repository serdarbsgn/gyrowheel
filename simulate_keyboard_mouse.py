import pynput.keyboard as pyk
import pynput.mouse as pym
def simulate_km(data,previous_button_state,keyboard,mouse):
    def handle_press(keycode):
        if keycode == "":
            return
        elif keycode.startswith("c_"):
            keyboard.press(pyk.Key.ctrl)
            handle_press(keycode[2:])
            keyboard.release(pyk.Key.ctrl)
        elif keycode.startswith("a_"):
            keyboard.press(pyk.Key.alt)
            handle_press(keycode[2:])
            keyboard.release(pyk.Key.alt)
        elif keycode.startswith("s_"):
            keyboard.press(pyk.Key.shift)
            handle_press(keycode[2:])
            keyboard.release(pyk.Key.shift)
        elif keycode.startswith("w_"):
            keyboard.press(pyk.Key.cmd)     
            handle_press(keycode[2:])
            keyboard.release(pyk.Key.cmd)
        elif keycode == "space":
            keyboard.tap(pyk.Key.space)
        elif keycode == "backspace":
            keyboard.tap(pyk.Key.backspace) 
        elif keycode == "enter":
            keyboard.tap(pyk.Key.enter)
        elif keycode == "m_next":
            keyboard.tap(pyk.Key.media_next),
        elif keycode == "m_previous":
            keyboard.tap(pyk.Key.media_previous)
        elif keycode == "m_play":
            keyboard.tap(pyk.Key.media_play_pause)
        elif keycode == "m_vol_down":
            keyboard.tap(pyk.Key.media_volume_down)
        elif keycode == "m_vol_up":
            keyboard.tap(pyk.Key.media_volume_up)
        elif keycode == "m_vol_mute":
            keyboard.tap(pyk.Key.media_volume_mute)
        elif keycode == "caps":
            keyboard.tap(pyk.Key.caps_lock)
        elif keycode == "tab":
            keyboard.tap(pyk.Key.tab)
        elif keycode == "del":
            keyboard.tap(pyk.Key.delete)
        elif keycode == "esc":
            keyboard.tap(pyk.Key.esc)
        elif keycode == "ar_up":
            keyboard.tap(pyk.Key.up)
        elif keycode == "ar_down":
            keyboard.tap(pyk.Key.down)
        elif keycode == "ar_left":
            keyboard.tap(pyk.Key.left)
        elif keycode == "ar_right":
            keyboard.tap(pyk.Key.right)

    if data[2] != 0 and data[3]!= 0:
        mouse.move(data[2],data[3])
    handle_press(data[0])

    if data[1]:
        keyboard.type(data[1])

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
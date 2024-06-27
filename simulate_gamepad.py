import vgamepad as vg

def simulate_gamepad(gamepad,inputs,full):
    gamepad.left_trigger_float(value_float=inputs["LT"])
    gamepad.right_trigger_float(value_float=inputs["RT"])
    gamepad.left_joystick(x_value= inputs["SP"], y_value=inputs["SR"])
    if full:
        gamepad.press_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_LEFT_SHOULDER) if inputs["LB"]==1 else gamepad.release_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_LEFT_SHOULDER)
        gamepad.press_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_LEFT_THUMB) if inputs["L3"]==1 else gamepad.release_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_LEFT_THUMB)

        gamepad.press_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_RIGHT_SHOULDER) if inputs["RB"]==1 else gamepad.release_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_RIGHT_SHOULDER)
        gamepad.press_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_RIGHT_THUMB) if inputs["R3"]==1 else gamepad.release_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_RIGHT_THUMB)

        gamepad.press_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_BACK) if inputs["BC"]==1 else gamepad.release_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_BACK)
        gamepad.press_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_START) if inputs["ST"]==1 else gamepad.release_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_START)

        gamepad.press_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_Y) if inputs["Y"]==1 else gamepad.release_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_Y)
        gamepad.press_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_X) if inputs["X"]==1 else gamepad.release_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_X)
        gamepad.press_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_A) if inputs["A"]==1 else gamepad.release_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_A)
        gamepad.press_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_B) if inputs["B"]==1 else gamepad.release_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_B)

        gamepad.press_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_DPAD_UP) if inputs["AU"]==1 else gamepad.release_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_DPAD_UP)
        gamepad.press_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_DPAD_LEFT) if inputs["AL"]==1 else gamepad.release_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_DPAD_LEFT)
        gamepad.press_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_DPAD_RIGHT) if inputs["AR"]==1 else gamepad.release_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_DPAD_RIGHT)
        gamepad.press_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_DPAD_DOWN) if inputs["AD"]==1 else gamepad.release_button(vg.XUSB_BUTTON.XUSB_GAMEPAD_DPAD_DOWN)
        gamepad.right_joystick(x_value=inputs["ARX"],y_value=-inputs["ARY"])
    gamepad.update()
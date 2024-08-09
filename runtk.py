import tkinter as tk
import blcFwdtk,blctk,udpConntk
root = None
program_var = None

def on_start_button_click():
    selected_program = program_var.get()
    if selected_program == "udpConntk":
        root.destroy()
        udpConntk.main()
    elif selected_program == "blctk":
        root.destroy()
        blctk.main()
    else:
        root.destroy()
        blcFwdtk.main()

def main():
    global root,program_var
    root = tk.Tk()
    root.title("Select a Connection Method")

    program_var = tk.StringVar(value="udpConntk")

    tk.Label(root, text="Select a program to run:").pack(pady=10)

    programs = {"Network":"udpConntk","Bluetooth":"blctk","Bluetooth Forwarded":"blcFwdtk" }
    for k,program in programs.items():
        tk.Radiobutton(root, text=k, variable=program_var, value=program).pack(anchor=tk.W)

    tk.Button(root, text="Start", command=on_start_button_click).pack(pady=20)

    root.mainloop()
if __name__ == '__main__':
    main()

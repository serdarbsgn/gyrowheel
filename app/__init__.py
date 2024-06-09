import flask
import vgamepad as vg
app = flask.Flask(__name__)
gamepad = vg.VX360Gamepad()

if __name__ == "__main__":
    # Set the host and port
    host = "0.0.0.0"
    port = 5000

    # Run Flask app
    app.run(host=host,port=port)
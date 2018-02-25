# A simple library for capturing USB Xbox-one controller events on a RaspberryPi

I take no credit for the the Xbox controller driver. It was built in-to the version of Raspbian I was running (v9: stretch). I simply wrote a quick java app to read from /dev/input/jsX and bind a handler method to various button/joystick events.

Included is a simple main-class that uses input from a usb-connected XboxOne controller to control some motors on the Pi's GPIO pins.

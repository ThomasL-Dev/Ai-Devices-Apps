try:
    from pynput.mouse import Button, Controller, Listener
except Exception as e:
    print("Pynput import mouse error : " + str(e))
    import os
    os.system('export DISPLAY=":0"')
    os.system('xhost local:root')
    from pynput.mouse import Button, Controller, Listener

try:
    from binaries.obj.HandlerObject import HandlerObject
except:
    from ....Computer.binaries.obj.HandlerObject import HandlerObject
# ========================================== FIN DES IMPORTS ========================================================= #


class MouseHandler(HandlerObject):

    def __init__(self, kernel=None, logger=None):
        HandlerObject.__init__(self, name="Mouse", logger=logger)

        self.kernel = kernel
        self.logger = logger

        # init mouse
        try:
            self._mouse = Controller()
            self.BTN_CLICK_LEFT = Button.left
            self.BTN_CLICK_RIGHT = Button.right
            self.BTN_CLICK_WHEEL = Button.middle
        except:
            self.logger.error("Mouse controller couldn't be initialized")


    def move_cursor_to_position(self, x: int, y: int):
        self._mouse.position = x, y


    def move_cursor_relativly(self, x: int, y: int):
        self._mouse.move(x, y)


    def click(self, btn, time: int=1):
        self._mouse.click(btn, time)


    def get_positions(self):
        pos = self._mouse.position
        x = pos[0]
        y = pos[1]
        return x, y



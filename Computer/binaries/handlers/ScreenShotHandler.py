import os
import time
import pyscreenshot
try:
    from binaries.obj.HandlerObject import HandlerObject
except:
    from ....Computer.binaries.obj.HandlerObject import HandlerObject
# ========================================== FIN DES IMPORTS ========================================================= #


class ScreenShotHandler(HandlerObject):


    def __init__(self, kernel=None, logger=None):
        HandlerObject.__init__(self, name="Screenshot", logger=logger)

        self.kernel = kernel
        self.logger = logger

        # screenshot name
        self._screenshot_name = "screen.png"

        # screenshot previous name
        self._previous_screenshot_name = "prev_screen.png"

        # init screenshot object
        self.screenshot_obj = pyscreenshot

        # delay
        self.take_screenshot_delay = 2


    def on_handling(self):
        while True:

            self.take_screenshot(os.path.join(self.kernel.web_img_path, self._screenshot_name))

            time.sleep(self.take_screenshot_delay)


    def take_screenshot(self, file_name: str):
        self.screenshot_obj.grab().save(file_name)


    def remove_screenshot(self, file_name: str):
        os.remove(file_name)


    def _check_if_sc_taked(self):
        pass





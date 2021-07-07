try:
    from binaries.web.page.BasePage import BasePage
except:
    from ....Computer.binaries.web.page.BasePage import BasePage

import sys
import subprocess
# ========================================== FIN DES IMPORTS ========================================================= #


class Shutdown(BasePage):

    def on_get(self):
        self.power_off_func()
        self.redirect("/")


    def on_post(self):
        self.power_off_func()
        self.redirect("/")


    def power_off_func(self):
        try:
            if sys.platform == "linux":
                subprocess.call(["poweroff"])

            elif sys.platform == "win32":
                subprocess.call(["shutdown", "/s", "/t", "15"])

            self.logger.log("Computer is shutting down")

        except Exception as e:
            self.logger.error("Error while shutting down computer -- " + str(e))
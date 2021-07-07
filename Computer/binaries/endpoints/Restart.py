try:
    from binaries.web.page.BasePage import BasePage
except:
    from ....Computer.binaries.web.page.BasePage import BasePage

import sys
import subprocess
# ========================================== FIN DES IMPORTS ========================================================= #




class Restart(BasePage):

    def on_get(self):
        self.restart_func()
        self.redirect("/")


    def on_post(self):
        self.restart_func()
        self.redirect("/")


    def restart_func(self):
        try:
            if sys.platform == "linux":
                subprocess.call(["reboot"])

            elif sys.platform == "win32":
                subprocess.call(["shutdown", "/r", "/t", "15"])

            self.logger.log("Computer is restarting")

        except Exception as e:
            self.logger.error("Error while restarting computer -- " + str(e))
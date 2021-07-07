import time

try:
    from binaries.web.page.BasePage import BasePage
except:
    from ....Computer.binaries.web.page.BasePage import BasePage

# ========================================== FIN DES IMPORTS ========================================================= #


class Restart_app(BasePage):

    def on_get(self):
        self.redirect("/")
        time.sleep(2)
        self.restart_kernel_func()


    def on_post(self):
        self.redirect("/")
        time.sleep(2)
        self.restart_kernel_func()


    def restart_kernel_func(self):
        try:
            self.kernel.restart()
            self.logger.log("Restarting Ai app")

        except Exception as e:
            self.logger.error("Error while restarting app -- " + str(e))
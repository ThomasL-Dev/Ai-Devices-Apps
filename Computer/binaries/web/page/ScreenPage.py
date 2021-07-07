try:
    from binaries.web.page.BasePage import BasePage
except:
    from .....Computer.binaries.web.page.BasePage import BasePage
# ========================================== FIN DES IMPORTS ========================================================= #




class ScreenPage(BasePage):


    def on_get(self):
        self.render("screen.html",
                    dev_name=self.kernel.device_infos_handler.get_device_name(),
                    )


    def on_post(self):
        self.render("screen.html",
                    dev_name=self.kernel.device_infos_handler.get_device_name(),
                    )

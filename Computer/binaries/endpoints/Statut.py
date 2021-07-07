try:
    from binaries.web.page.BasePage import BasePage
except:
    from ....Computer.binaries.web.page.BasePage import BasePage

# ========================================== FIN DES IMPORTS ========================================================= #


class Statut(BasePage):

    def set_default_headers(self):
        self.set_header("Content-Type", 'application/json')

    def on_get(self):
        self.statut_func()


    def on_post(self):
        self.statut_func()


    def statut_func(self):
        try:
            self.write(
                dict({
                "os_started_since": str(None),
                "internet_statut": str(None),
                "foreground_app": str(None),
                "sound_lvl": str(None),
            }))
        except Exception as e:
            self.logger.error("Error while getting statut -- " + str(e))
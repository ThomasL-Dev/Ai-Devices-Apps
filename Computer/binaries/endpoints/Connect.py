try:
    from binaries.web.page.BasePage import BasePage
except:
    from ....Computer.binaries.web.page.BasePage import BasePage

# ========================================== FIN DES IMPORTS ========================================================= #


class Connect(BasePage):

    def on_get(self):
        self.connect_func()
        self.redirect("/")


    def on_post(self):
        self.connect_func()
        self.redirect("/")


    def connect_func(self):
        try:
            ip_arg = self.get_arg_by_name('ip')
            port_arg = self.get_arg_by_name('port')

            if ip_arg is not None:
                self.kernel.ai_server_connection_handler.set_ip(ip_arg)
                self.logger.log("Ip for server connection updated to : " + str(ip_arg))

            if port_arg is not None:
                self.kernel.ai_server_connection_handler.set_port(port_arg)
                self.logger.log("Port for server connection updated to : " + str(port_arg))

        except Exception as e:
            self.logger.error("Error while setting up IP or PORT for server connection -- " + str(e))
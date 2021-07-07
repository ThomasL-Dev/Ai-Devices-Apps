import time

try:
    from binaries.web.page.BasePage import BasePage
except:
    from .....Computer.binaries.web.page.BasePage import BasePage
# ========================================== FIN DES IMPORTS ========================================================= #




class ProcessusPage(BasePage):


    def on_get(self):
        self.render("processus.html",
                    dev_name=self.kernel.device_infos_handler.get_device_name(),
                    process_list=self.kernel.processus_handler.get_processus_list(),
                    )


    def on_post(self):
        # kill process
        proc_name = self.get_arg_by_name('name-value')
        process_found = self.kernel.processus_handler.find_process_in_list(proc_name)
        self.kernel.processus_handler.kill_process(process_found)

        self.render("processus.html",
                    dev_name=self.kernel.device_infos_handler.get_device_name(),
                    process_list=self.kernel.processus_handler.get_processus_list(),
                    )

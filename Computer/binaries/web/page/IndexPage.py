try:
    from binaries.web.page.BasePage import BasePage
except:
    from .....Computer.binaries.web.page.BasePage import BasePage
# ========================================== FIN DES IMPORTS ========================================================= #




class IndexPage(BasePage):


    def on_get(self):
        self.render("index.html",
                    dev_name=self.kernel.device_infos_handler.get_device_name(),
                    connected_user=self.kernel.device_infos_handler.get_user_connected_name(),
                    user_connected_since=self.kernel.device_infos_handler.get_user_connected_since(),

                    device_os=self.kernel.device_infos_handler.get_device_os(),
                    device_ip=self.kernel.device_infos_handler.get_device_ip(),
                    device_booted_since=self.kernel.device_infos_handler.get_device_booted_since(),
                    device_version=self.kernel.device_infos_handler.get_device_version(),
                    os_architecture = self.kernel.device_infos_handler.get_device_architecture(),

                    cpu_usage=self.kernel.device_infos_handler.get_cpu_usage_in_percent(),
                    cpu_count=self.kernel.device_infos_handler.get_cpu_count(),
                    cpu_architecture=self.kernel.device_infos_handler.get_cpu_architecture(),

                    ram_used=self.kernel.device_infos_handler.get_ram_used_in_percent(),
                    ram_free=self.kernel.device_infos_handler.get_ram_available_in_percent(),

                    disk_list=self.kernel.disks_handler.get_disks_list(),
                    version=self.kernel.get_version(),

                    )


    def on_post(self):
        self.render("index.html",
                    dev_name=self.kernel.device_infos_handler.get_device_name(),
                    connected_user=self.kernel.device_infos_handler.get_user_connected_name(),
                    user_connected_since=self.kernel.device_infos_handler.get_user_connected_since(),

                    device_os=self.kernel.device_infos_handler.get_device_os(),
                    device_ip=self.kernel.device_infos_handler.get_device_ip(),
                    device_booted_since=self.kernel.device_infos_handler.get_device_booted_since(),
                    device_version=self.kernel.device_infos_handler.get_device_version(),
                    os_architecture=self.kernel.device_infos_handler.get_device_architecture(),

                    cpu_usage=self.kernel.device_infos_handler.get_cpu_usage_in_percent(),
                    cpu_count=self.kernel.device_infos_handler.get_cpu_count(),
                    cpu_architecture=self.kernel.device_infos_handler.get_cpu_architecture(),

                    ram_used=self.kernel.device_infos_handler.get_ram_used_in_percent(),
                    ram_free=self.kernel.device_infos_handler.get_ram_available_in_percent(),

                    disk_list=self.kernel.disks_handler.get_disks_list(),
                    version=self.kernel.get_version(),
                    )

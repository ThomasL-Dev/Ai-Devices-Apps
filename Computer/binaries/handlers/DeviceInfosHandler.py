import sys
import platform
import socket
import psutil
from datetime import datetime
try:
    from binaries.obj.HandlerObject import HandlerObject
except:
    from ....Computer.binaries.obj.HandlerObject import HandlerObject
# ========================================== FIN DES IMPORTS ========================================================= #


class DeviceInfosHandler(HandlerObject):

    def __init__(self, kernel=None, logger=None):
        HandlerObject.__init__(self, name="Device Infos", logger=logger)

        self.kernel = kernel
        self.logger = logger


        # init system infos
        self._system = platform.uname()

        # init user infos
        self._user_infos = psutil.users()[0]


        # INIT UNUPDATABLE DEVICE INFOS
        self.logger.log("--- DEVICE INFOS ---")

        self._device_name = str(self._socket_device_name())
        self.logger.log("Device name : " + self._device_name)

        self._device_ip = str(self.get_device_ip())
        self.logger.log("Device ip : " + self._device_ip)

        self._device_os = str(self._system.system)
        self.logger.log("Device os : " + self._device_os)

        self._device_release = str(self._system.release)
        self.logger.log("Device release : " + self._device_release)

        self._device_version = str(self._system.version)
        self.logger.log("Device version : " + self._device_version)

        self._device_architecture = str(self._system.machine)
        self.logger.log("Device architecture : " + self._device_architecture)

        self._device_processor_architecture = str(self._system.processor)
        self.logger.log("Device processor architecture : " + self._device_processor_architecture)

        self._user_connected_name = self._user_infos.name
        self.logger.log("Device connected user : " + self._user_connected_name)

        self._user_connected_since = str(datetime.fromtimestamp(self._user_infos.started).strftime("%H:%M:%S %d/%m/%Y"))
        self.logger.log("Device user connected since : " + self._user_connected_since)

        self._device_boot_time = str(datetime.fromtimestamp(psutil.boot_time()).strftime("%H:%M:%S %d/%m/%Y"))
        self.logger.log("Device booted since : " + self._device_boot_time)
        self.logger.log("-" * len("--- DEVICE INFOS ---"))


    ### CPU ###
    def get_cpu_count(self):
        return str(psutil.cpu_count())

    def get_cpu_usage_in_percent(self):
        return str(psutil.cpu_percent(interval=0.1)) + " %"

    def get_cpu_architecture(self):
        return str(self._device_processor_architecture)


    ### RAM ###
    def get_ram_available_in_percent(self):
        ram_infos = psutil.virtual_memory()
        return str(int(int(ram_infos.available) / int(ram_infos.total) * 100)) + " %"

    def get_ram_used_in_percent(self):
        ram_infos = psutil.virtual_memory()
        return str(int(ram_infos.percent)) + " %"


    ### USER ###
    def get_user_connected_since(self):
        return self._user_connected_since

    def get_user_connected_name(self):
        return self._user_connected_name


    ### DEVICE ###
    def get_device_booted_since(self):
        return self._device_boot_time

    def get_device_name(self):
        return str(self._device_name)

    def get_device_os(self):
        return str(self._device_os)

    def get_device_release(self):
        return str(self._device_release)

    def get_device_version(self):
        return str(self._device_version)

    def get_device_architecture(self):
        return str(self._device_architecture)

    def get_device_ip(self):
        try:
            dns = ("8.8.8.8", 80)
            sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
            sock.connect(dns)
            ip = sock.getsockname()[0]
            sock.close()
        except:
            ip = socket.gethostbyname(self._socket_device_name())
        return ip


    ### UTILS ###
    def _socket_device_name(self):
        return socket.gethostname()

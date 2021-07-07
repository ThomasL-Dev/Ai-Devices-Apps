import os
import sys
import time

try:
    from binaries.Shortcut import Shortcut
    from binaries.Logger import Logger
    from binaries.handlers.NetworkHandler import NetworkHandler
    from binaries.handlers.DeviceInfosHandler import DeviceInfosHandler
    from binaries.handlers.ScreenShotHandler import ScreenShotHandler
    from binaries.handlers.ProcessusHandler import ProcessusHandler
    from binaries.handlers.MouseHandler import MouseHandler
    from binaries.handlers.KeyboardHandler import KeyboardHandler
    from binaries.handlers.DisksHandler import DisksHandler
    from binaries.handlers.FtpHandler import FtpHandler
    from binaries.web.WebHandler import WebHandler
    from binaries.handlers.AiServerConnectionHandler import AiServerConnectionHandler

except:
    from ...Computer.binaries.Shortcut import Shortcut
    from ...Computer.binaries.Logger import Logger
    from ...Computer.binaries.handlers.NetworkHandler import NetworkHandler
    from ...Computer.binaries.handlers.DeviceInfosHandler import DeviceInfosHandler
    from ...Computer.binaries.handlers.ScreenShotHandler import ScreenShotHandler
    from ...Computer.binaries.handlers.ProcessusHandler import ProcessusHandler
    from ...Computer.binaries.handlers.MouseHandler import MouseHandler
    from ...Computer.binaries.handlers.DisksHandler import DisksHandler
    from ...Computer.binaries.handlers.KeyboardHandler import KeyboardHandler
    from ...Computer.binaries.handlers.FtpHandler import FtpHandler
    from ...Computer.binaries.web.WebHandler import WebHandler
    from ...Computer.binaries.handlers.AiServerConnectionHandler import AiServerConnectionHandler

# ========================================== FIN DES IMPORTS ========================================================= #


class Kernel:

    # PATHS
    main_path = os.getcwd()

    cfg_file_path = os.path.join(main_path, "cfg.ini")

    binaries_path = os.path.join(main_path, "binaries")

    web_img_path = os.path.join(binaries_path, "web", "static", "img")

    temp_path = os.path.join(binaries_path, "temp")
    # =====

    def __init__(self):

        # INIT LOGGER
        self.logger = Logger()
        self.logger.log("STARTED", startwith="\n")
        self.logger.log("Version : " + str(self.get_version()), startwith="\n")
        # =====

        # create shortcut if not exist
        shortcut = Shortcut(logger=self.logger)
        shortcut.create()

        # temp stuff
        self._create_temp_folder()
        # ==========

        # INIT HANDLERS
        self.device_infos_handler = DeviceInfosHandler(kernel=self, logger=self.logger)

        self.screenshot_handler = ScreenShotHandler(kernel=self, logger=self.logger)

        self.disks_handler = DisksHandler(kernel=self, logger=self.logger)

        self.mouse_handler = MouseHandler(kernel=self, logger=self.logger)

        self.keyboard_handler = KeyboardHandler(kernel=self, logger=self.logger)

        self.processus_handler = ProcessusHandler(kernel=self, logger=self.logger)

        self.ftp_handler = FtpHandler(kernel=self, logger=self.logger)

        self.web_handler = WebHandler(kernel=self, logger=self.logger)

        self.network_handler = NetworkHandler(kernel=self, logger=self.logger)

        self.ai_server_connection_handler = AiServerConnectionHandler(kernel=self, logger=self.logger)
        # =====


    def start(self):
        self.start_handlers()


    def start_handlers(self):
        """ start handlers """
        self.device_infos_handler.start()

        self.network_handler.start()

        self.ftp_handler.start()

        self.processus_handler.start()

        self.screenshot_handler.start()

        self.mouse_handler.start()

        self.disks_handler.start()

        self.keyboard_handler.start()

        self.web_handler.start()

        self.ai_server_connection_handler.start()
        self.ai_server_connection_handler.join()


    def restart(self):
        """Restarts the current program.
        Note: this function does not return. Any cleanup action (like
        saving data) must be done before calling this function."""
        python = sys.executable
        os.execl(python, python, *sys.argv)


    def get_version(self):
        fname = os.path.join(os.getcwd(), 'version.txt')
        if os.path.exists(fname):
            with open(fname, 'r+') as v_file:
                version = v_file.readline()
                v_file.close()
            return version
        else:
            return "0"


    def _create_temp_folder(self):
        """ temp folder to stock temp stuff """
        if not os.path.exists(self.temp_path):
            os.mkdir(self.temp_path)



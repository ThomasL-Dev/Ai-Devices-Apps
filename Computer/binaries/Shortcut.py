import os
import sys

class Shortcut:

    def __init__(self, logger=None):
        self.logger = logger

        self.boot_file = "boot.pyw"

        self.shortcut_name = "ai_desktop_app"

        self.exec_path = os.getcwd()

        if "win32" in sys.platform:
            self.extension = ".bat"

            self.path_to_save = os.path.join(os.getenv('APPDATA'), "Microsoft", "Windows", "Start Menu", "Programs", "Startup")

            self.shortcut_value = 'cd "' + str(self.exec_path) + '"\nstart pythonw ' + str(self.boot_file)

        else:
            self.extension = ".sh"

            self.path_to_save = "/home/" + os.path.expanduser('~/Desktop').replace("root", os.getlogin())

            self.shortcut_value = '#!/bin/bash\ncd "' + str(self.exec_path) + '"\nsudo python3 ' + str(self.boot_file)

        self.shortcut_path = os.path.join(self.path_to_save, self.shortcut_name + self.extension)



    def create(self):
        if self.logger is not None:
            try:
                if not os.path.exists(self.shortcut_path):
                    try:
                        self._create_shortcut_file(self.shortcut_path, self.shortcut_value)
                    except:
                        self._create_safe_shortcut()

            except:
                try:
                    self._create_safe_shortcut()
                except Exception as e:
                    self.logger.error("Error while creating shortcut -- error : " + str(e))


    def _create_shortcut_file(self, path: str, value: str):
        with open(path, "w+") as shortcut_file:
            shortcut_file.write(value)
            shortcut_file.close()
        if "linux" in sys.platform:
            os.system('chmod u+x "' + str(path) + '"')
        self.logger.log("Shortcut created in '" + path + "'")


    def _create_safe_shortcut(self):
        path_safe = os.path.join(os.getcwd(), self.shortcut_name + self.extension)
        if not os.path.exists(path_safe):
            self._create_shortcut_file(path_safe, self.shortcut_value)





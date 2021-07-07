import configparser
import os
# ========================================== FIN DES IMPORTS ========================================================= #


class ConfigFile:

    def __init__(self, kernel, logger):
        self.kernel = kernel
        self.logger = logger

        self.config = configparser.ConfigParser()

        self._check_if_exist()



    def get_ip(self):
        self.config.read(self.kernel.cfg_file_path)
        return self.config['SERVER_ADRR']['ip']

    def get_port(self):
        self.config.read(self.kernel.cfg_file_path)
        return self.config['SERVER_ADRR']['port']


    def update_ip(self, ip: str):
        self.config.read(self.kernel.cfg_file_path)
        self.config['SERVER_ADRR']['ip'] = ip
        self._write_in_file()


    def update_port(self, port: str):
        self.config.read(self.kernel.cfg_file_path)
        self.config['SERVER_ADRR']['port'] = port
        self._write_in_file()



    def _write_in_file(self):
        with open(self.kernel.cfg_file_path, "w+") as cfg_file:
            self.config.write(cfg_file)
            cfg_file.close()

    def _create_base_file(self):
        self.config.read(self.kernel.cfg_file_path)
        self.config['SERVER_ADRR'] = {
            'ip': '0.0.0.0',
            'port': '33000',
        }
        self._write_in_file()

    def _check_if_exist(self):
        if not os.path.exists(self.kernel.cfg_file_path):
            self._create_base_file()




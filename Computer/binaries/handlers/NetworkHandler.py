import time, os, json
import socket

try:
    from binaries.obj.HandlerObject import HandlerObject
except:
    from ....Computer.binaries.obj.HandlerObject import HandlerObject
# ========================================== FIN DES IMPORTS ========================================================= #


class NetworkHandler(HandlerObject):

    def __init__(self, kernel=None, logger=None):
        HandlerObject.__init__(self, name="Network", logger=logger)

        self.kernel = kernel

        # init delay
        self.refresh_delay = 2

        # init statut flags
        self.statut_flags = {
            'OK': "Connecté à internet",
            'NOK': "Déconnecté d'internet"
        }
        # previous statut to not repeat log
        self.previous_statut = None
        # init statut
        self.statut = None

        # google dns & web port
        self.dns_ip = "8.8.8.8"
        self.dns_port = 80

        # init socket
        self.network_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

        # init error
        self.error = None
        # previous error to not repeat log
        self.previous_error = None


    def on_handling(self):
        """ check network statut """
        while True:
            try:
                self._connect_socket()

                self._set_statut('OK')

            except Exception as e:
                self._set_statut('NOK')

            time.sleep(self.refresh_delay)


    def get_statut(self):
        return self.statut


    def get_error(self):
        return self.error


    def _set_error(self, error: str):
        self.error = error
        if self.error != self.previous_error:
            self.previous_error = self.error
            self.logger.error(error)


    def _set_statut(self, statut: str):
        flag_statut = self.statut_flags[str(statut)]
        self.statut = flag_statut
        if self.statut != self.previous_statut:
            self.previous_statut = self.statut
            self.logger.info(flag_statut)


    def _connect_socket(self):
        self.network_socket.connect((self.dns_ip, self.dns_port))


    def _close_socket(self):
        self.network_socket.close()





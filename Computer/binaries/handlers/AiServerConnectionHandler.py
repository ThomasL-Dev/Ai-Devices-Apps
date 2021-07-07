import time
import socket

try:
    from binaries.obj.HandlerObject import HandlerObject
    from binaries.ConfigFile import ConfigFile
except:
    from ....Computer.binaries.obj.HandlerObject import HandlerObject
    from ....Computer.binaries.ConfigFile import ConfigFile

# ========================================== FIN DES IMPORTS ========================================================= #


class AiServerConnectionHandler(HandlerObject):

    def __init__(self, kernel=None, logger=None):
        HandlerObject.__init__(self, name="AI Server Connection", logger=logger)

        self.kernel = kernel
        self.logger = logger

        # INIT cfg file
        self.config_file = ConfigFile(kernel=self.kernel, logger=self.logger)

        # init ip & port server
        self.server_ip = self.config_file.get_ip()
        try:
            self.server_port = int(self.config_file.get_port())
        except ValueError: # avoid non int port error
            self.server_port = 0

        self._ip_updated = True
        self._port_updated = True

        # init  server socket
        self.server_socket = None
        self.is_connected = False
        self.is_reconnecting = False

        # init error
        self.error = None
        # previous error to not repeat log
        self.previous_error = None

        # init statut flags
        self.statut_flags = {
            'OK': "Connecté au serveur",
            'NOK': "Déconnecté du serveur"
        }
        # init statut
        self.statut = None
        # previous statut to not repeat log
        self.previous_statut = None

        # keep alive delay
        self.reconnecting_delay = 2

        # init server connection if adress is not none
        if self.server_ip is not None or self.server_port is not None:
            # connect to server
            self._connect_to_server()

        else:
            self.logger.error("Adresse Ip ou Port non défini", error_location="Server Connection")


    def on_handling(self):
        sleep_delay = 1
        while True:
            if self.is_connected:
                try:
                    """ if connected everything ok 
                    just send a 'pass' code to server to get error if disconnected
                    """
                    code = 'pass'

                    self._send_code(code)
                    self._set_statut('OK')

                    time.sleep(sleep_delay)

                except Exception as e:
                    """ else error try to reconnect to server """
                    error = self._decrypt_error(str(e))
                    self._set_error(error)
                    self.close_socket_server()
                    self._set_statut('NOK')

            elif not self.is_reconnecting and not self.is_connected:
                self._reconnecting()


    def set_ip(self, ip: str):
        self.config_file.update_ip(str(ip))
        self.server_ip = str(ip)
        self._ip_updated = True


    def set_port(self, port: str):
        self.config_file.update_port(str(port))
        self.server_port = int(port)
        self._port_updated = True


    def get_statut(self):
        return self.statut


    def get_error(self):
        return self.error


    def close_socket_server(self):
        """ close connection to server & reset it to None """
        self.server_socket.close()
        self.server_socket = None
        self.is_connected = False


    def _connect_to_server(self):
        try:
            """ connecting to server """
            # setting socket server
            self.server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            # set timeout to not be long
            self.server_socket.settimeout(5)
            # init server adress
            server_address = (self.server_ip, int(self.server_port))

            # log stuff
            if self._ip_updated or self._ip_updated:
                self.logger.info("Connexion au serveur : " + str(self.server_ip) + ":" + str(self.server_port))
                self._ip_updated = False
                self._ip_updated = False

            # trying to connect to server
            self.server_socket.connect(server_address)

            # sending header to comfirm connexion
            self.server_socket.send('App-Type:controllable'.encode("utf8"))

            # if device is connected is not reconnecting anymore
            self.is_reconnecting = False
            # setting to connected
            self.is_connected = True

        except Exception as e:
            """ else connection to server fail """
            # setting connected to false cuz it not connected to server
            self.is_connected = False
            # log stuff
            error = self._decrypt_error(str(e))
            error = "Impossible de se connecté au serveur : " + str(self.server_ip) + ":" + str(self.server_port) + " -- Error : " + error
            self._set_error(error)


    def _reconnecting(self):
        """ trying to reconnect to server if connection crashed """
        # see log once
        log_reconnected_writed = False
        # reset is_connected cuz if a exception raise when sending a code client is not connected anymore
        self.is_connected = False
        # setting reconnecting statut to True to not launch multiple time this function
        self.is_reconnecting = True
        # trying to reconnect to server
        while not self.is_connected:
            # see log once
            if not log_reconnected_writed:
                self.logger.info("Reconnection au serveur")
                log_reconnected_writed = True
            # trying to conenct to server with 1sc of delay
            self._connect_to_server()
            time.sleep(self.reconnecting_delay)


    def _send_code(self, code: str):
        code = "code:{}".format(str(code))
        self.server_socket.send(code.encode("utf8"))


    def _set_error(self, error: str):
        self.error = error
        if self.error != self.previous_error:
            self.previous_error = self.error
            self.logger.error(error, error_location="Server Connection")


    def _set_statut(self, statut: str):
        flag_statut = self.statut_flags[str(statut)]
        self.statut = flag_statut
        if self.statut != self.previous_statut:
            self.previous_statut = self.statut
            self.logger.info(flag_statut)


    def _decrypt_error(self, error: str):
        if "] " in error:
            return error.split("] ")[1]
        else:
            return error

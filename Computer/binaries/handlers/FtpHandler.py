# pip3 install pyftpdlib
from pyftpdlib.authorizers import DummyAuthorizer
from pyftpdlib.handlers import FTPHandler
from pyftpdlib.servers import FTPServer
from pyftpdlib.log import config_logging
import logging
config_logging(level=logging.CRITICAL)
import sys

try:
    from binaries.obj.HandlerObject import HandlerObject
except:
    from ....Computer.binaries.obj.HandlerObject import HandlerObject
# ========================================== FIN DES IMPORTS ========================================================= #


class FtpHandler(HandlerObject):

    def __init__(self, kernel=None, logger=None):
        HandlerObject.__init__(self, name="FTP", logger=logger)

        self.kernel = kernel
        self.logger = logger

        if sys.platform == "win32":
            self.root_path = "C:/"
        else:
            self.root_path = "/home/"

        # init authorizer
        self.authorizer = DummyAuthorizer()

        # init ftp server
        self.handler = FTPHandler



    def on_handling(self):
        self._add_path_to_user()
        self._set_authorizer()
        self._start_server()


    def _start_server(self):
        server = FTPServer(("0.0.0.0", 21), self.handler)
        server.set_reuse_addr()
        server.serve_forever()

    def _add_path_to_user(self):
        try:
            self.authorizer.add_anonymous(self.root_path)
        except Exception as e:
            self.logger.error(str(e))

    def _set_authorizer(self):
        self.handler.authorizer = self.authorizer




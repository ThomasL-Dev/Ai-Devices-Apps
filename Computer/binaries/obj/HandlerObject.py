from threading import Thread
# ========================================== FIN DES IMPORTS ========================================================= #


class HandlerObject(Thread):

    def __init__(self, name='Handler', logger=None):
        Thread.__init__(self)

        self.logger = logger

        self._NAME = name

        self.setDaemon(True)

    def on_handling(self):
        """ should be a while loop """
        pass

    def run(self):
        if self.logger is not None:
            try:
                self.logger.log("{} Handler started".format(self._NAME))
                self.on_handling()
            except Exception as e:
                self.logger.error("{} Handler -- ".format(self._NAME) + str(e))

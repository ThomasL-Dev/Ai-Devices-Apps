try:
    from binaries.obj.HandlerObject import HandlerObject
except:
    from ....Computer.binaries.obj.HandlerObject import HandlerObject
# ========================================== FIN DES IMPORTS ========================================================= #


class AppsHandler(HandlerObject):
    def __init__(self, kernel=None, logger=None):
        HandlerObject.__init__(self, name="Apps", logger=logger)

        self.kernel = kernel
        self.logger = logger

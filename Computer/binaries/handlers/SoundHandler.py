try:
    from binaries.obj.HandlerObject import HandlerObject
except:
    from ....Computer.binaries.obj.HandlerObject import HandlerObject
# ========================================== FIN DES IMPORTS ========================================================= #


class SoundHandler(HandlerObject):
    def __init__(self, kernel=None, logger=None):
        HandlerObject.__init__(self, name="Sound", logger=logger)

        self.kernel = kernel
        self.logger = logger

try:
    from binaries.Updater import Updater
except:
    from ..Computer.binaries.Updater import Updater
# start by update if needed
update = Updater()
update.start()
try:
    from binaries.Requierements import install_requierement
except:
    from ..Computer.binaries.Requierements import install_requierement
# install requierment if not already installed
install_requierement()
try:
    from binaries.Kernel import Kernel
except:
    from ..Computer.binaries.Kernel import Kernel
# ========================================== FIN DES IMPORTS ========================================================= #



if __name__ == '__main__':
    try:
        # init and start program
        kernel = Kernel()
        kernel.start()
    except KeyboardInterrupt:
        print("Exiting")

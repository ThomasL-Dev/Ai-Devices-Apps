from typing import NamedTuple
import subprocess
from psutil import Process
import sys
# ========================================== FIN DES IMPORTS ========================================================= #


class DiskInfo(NamedTuple):
    name: str
    used: str
    free: str
    total: str




class ProcessInfo(NamedTuple):

    name: str
    pid: str

    if "linux" in sys.platform:
        process_class: Process


    def terminate(self):
        if "win32" in sys.platform:
            subprocess.call(['taskkill', '/F', '/PID', str(self.pid)], creationflags=0x08000000)

        else:
            self.process_class.terminate()

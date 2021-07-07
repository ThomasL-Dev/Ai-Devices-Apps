import psutil
import sys
import os

try:
    from binaries.obj.HandlerObject import HandlerObject
    from binaries.obj.InfoObjects import ProcessInfo
except:
    from ....Computer.binaries.obj.HandlerObject import HandlerObject
    from ....Computer.binaries.obj.InfoObjects import ProcessInfo
# ========================================== FIN DES IMPORTS ========================================================= #


class ProcessusHandler(HandlerObject):

    def __init__(self, kernel=None, logger=None):
        HandlerObject.__init__(self, name="Processus", logger=logger)

        self.kernel = kernel
        self.logger = logger

        # init proc list
        self.process_list = []



    def get_processus_list(self):
        self._load_processus_in_list()
        return self.process_list


    def find_process_in_list(self, process_name: str):
        # psutil.Process(pid=1, name='systemd', status='sleeping', started='14:34:31')
        for proc in self.process_list:
            if str(process_name) in str(proc.name):
                return proc
            else:
                pass
        return None


    def kill_process(self, process: ProcessInfo):
        if process is not None:
            process.terminate()


    def _load_processus_in_list(self):
        if "win32" in sys.platform:
            self.process_list.clear()
            for _proc in os.popen('wmic process get description, processid').read().split("\n\n")[1:]:
                proc = _proc.split()
                if len(proc) == 2:
                    proc_name = proc[0]
                    proc_pid = proc[1]
                    self.process_list.append(ProcessInfo(proc_name, proc_pid))

        else:
            self.process_list.clear()
            for proc in psutil.process_iter(['pid', 'name']):
                self.process_list.append(ProcessInfo(proc.name(), proc.pid, proc))

import psutil
import sys

try:
    from binaries.obj.HandlerObject import HandlerObject
    from binaries.obj.InfoObjects import DiskInfo
except:
    from ....Computer.binaries.obj.HandlerObject import HandlerObject
    from ....Computer.binaries.obj.InfoObjects import DiskInfo
# ========================================== FIN DES IMPORTS ========================================================= #


class DisksHandler(HandlerObject):

    def __init__(self, kernel=None, logger=None):
        HandlerObject.__init__(self, name="Disks", logger=logger)

        self.kernel = kernel
        self.logger = logger

        self._temp_disks_info = []


    def get_disks_list(self):
        self._get_disks()
        for disk_name in self._temp_disks_info:
            try:
                used = self._get_disk_used_octet(disk_name)
                free = self._get_disk_free_octet(disk_name)
                total = self._get_disk_total_octet(disk_name)
                yield DiskInfo(disk_name, used, free, total)
            except:
                pass


    def _get_disks(self):
        self._temp_disks_info.clear()
        for disk in psutil.disk_partitions():
            self._temp_disks_info.append(disk.mountpoint)


    def _get_disk_total_octet(self, disk_name: str, converted=True):
        total = psutil.disk_usage(disk_name).total
        if converted:
            return self._disk_bytes2human(total)
        else:
            return total


    def _get_disk_used_octet(self, disk_name: str, converted=True):
        used = psutil.disk_usage(disk_name).used
        if converted:
            return self._disk_bytes2human(used)
        else:
            return used


    def _get_disk_free_octet(self, disk_name: str, converted=True):
        free = psutil.disk_usage(disk_name).free
        if converted:
            return self._disk_bytes2human(free)
        else:
            return free


    def _disk_bytes2human(self, n, format="%(value).1f %(symbol)s"):
        """Used by various scripts. See:
        http://goo.gl/zeJZl
        bytes2human(10000)
        '9.8K'
        bytes2human(100001221)
        '95.4M'
        """
        symbols = ('B', 'Ko', 'Mo', 'Go', 'To', 'Po', 'Eo', 'Zo', 'Yo')
        prefix = {}
        for i, s in enumerate(symbols[1:]):
            prefix[s] = 1 << (i + 1) * 10
        for symbol in reversed(symbols[1:]):
            if n >= prefix[symbol]:
                value = float(n) / prefix[symbol]
                return format % locals()
        return format % dict(symbol=symbols[0], value=n)









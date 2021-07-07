import datetime
import os
import sys
# ========================================== FIN DES IMPORTS ========================================================= #

class Logger:


    def __init__(self):
        self.log_folder = os.path.join(os.getcwd(), "Logs")



    def log(self, string: str, startwith="", endwith=""):
        date, time = self._get_date_time()
        log = str(startwith) + "[" + date + "-" + time + "]" + "[LOG] " + string + str(endwith)
        self._write_in_log_file(log)
        print(log)


    def info(self, string: str, startwith="", endwith=""):
        date, time = self._get_date_time()
        log = str(startwith) + "[" + date + "-" + time + "]" + "[INFO] " + string + str(endwith)
        self._write_in_log_file(log)
        print(log)


    def error(self, string: str, error_location: str=None):
        date, time = self._get_date_time()
        if error_location is not None:
            log = "[" + date + "-" + time + "]" + "[" + error_location + " ERROR] " + string
        else:
            log = "[" + date + "-" + time + "]" + "[ERROR] " + string

        self._write_in_log_file(log)
        print(log)


    def _write_in_log_file(self, log: str):
        # check logs folder exist
        self._check_if_logs_folder_exist()

        # init date
        date, __ = self._get_date_time()

        # init log file name & path
        log_file_name = str("log-" + date.replace("/", "-") + ".txt")
        log_file_path = os.path.join(os.getcwd(), "Logs", log_file_name)

        # check log file & create if necessary
        self._check_if_log_file_exist(log_file_path, date)

        # write in log file
        with open(log_file_path, "a+") as log_file:
            if "win32" in sys.platform:
                log_file.write("\n" + log)
            else:
                log_file.write(log + "\n")
            log_file.close()


    def _check_if_log_file_exist(self, file_name: str, date: str):
        if not os.path.exists(file_name):
            self._create_log_file(file_name, date)


    def _check_if_logs_folder_exist(self):
        if not os.path.exists(self.log_folder):
            os.mkdir(self.log_folder)


    def _create_log_file(self, file_name: str, date: str):
        _string = "### [ LOGS DU {} ] ###".format(date.upper())
        with open(file_name, "w+") as log_file:
            log_file.write("#" * len(_string) + "\n")
            log_file.write(_string + "\n")
            log_file.write("#" * len(_string) + "\n")
            log_file.close()


    def _get_date_time(self):
        now = datetime.datetime.now()
        current_time = now.strftime("%H:%M:%S")

        date_object = str(datetime.date.today()).split("-")
        y = date_object[0]
        m = date_object[1]
        d = date_object[2]
        connected_date = str(d + "/" + m + "/" + y)
        return connected_date, current_time
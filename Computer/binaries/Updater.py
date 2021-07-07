import os
import urllib.request
import zipfile
import sys
import time


class Updater:

    def __init__(self):
        # url version file
        self._version_file_url = "https://www.dropbox.com/s/6aqhp6znvst7mab/computer_version.txt?dl=1"
        # init version file
        self._version_installed_file = "version.txt"
        self._version_live_file = "live_version.txt"
        # init temp zip file
        self._tmp_zip_file = "tmp.zip"
        # url for dl the file
        self._url_zip = "https://www.dropbox.com/s/deleyxcki9cff7t/Computer.zip?dl=1"
        # extract file to this folder
        self._extract_folder = os.path.join(os.path.split(os.getcwd())[0])  # ".." to replace all file directly no need to copy etc

        # init version var
        self._live_version = None
        self._installed_version = None

    def start(self):
        if not os.path.exists('no_update.txt'):
            try:
                # on check & get version from live and installed file
                self._check_installed_version()
                self._check_live_version()

                if self._installed_version != self._live_version:
                    print("Starting to update")
                    # dl zip file
                    self._download_file(self._url_zip, self._tmp_zip_file)
                    # extart zip file
                    self._extract_zip_file(self._tmp_zip_file)
                    # remove zip file
                    self._remove_file(self._tmp_zip_file)
                    # write new version in installed file
                    self._write_version_installed_file()
                    print("Update finish")
                    print("Restarting...")
                    time.sleep(2)
                    python = sys.executable
                    os.execl(python, python, *sys.argv)
                else:
                    print("No need to update")
            except:
                print("Error while doing update")

    def _download_file(self, url: str, filename_output: str):
        try:
            filename, __ = urllib.request.urlretrieve(url, filename=filename_output)
            print("File '" + str(filename) + "' downloaded")
        except:
            print("Error while downloading '" + str(self._tmp_zip_file) + "' file")

    def _extract_zip_file(self, file_name: str):
        print("Extracting '" + file_name + "' folder")
        try:
            with zipfile.ZipFile(file_name, 'r') as zip_file:
                zip_file.extractall(self._extract_folder)
                zip_file.close()
                print("'" + file_name + "' file Extracted")
        except Exception as e:
            print("Error while extracting '" + str(file_name) + "' folder")

    def _remove_file(self, file_name: str):
        try:
            os.remove(file_name)
            print("File '" + file_name + "' removed")
        except:
            print("Error while removing '" + file_name + "' file")

    def _check_installed_version(self):
        if not os.path.exists(self._version_installed_file):
            """ si ya pas de fichier version installé on le crée """
            self._create_version_installed_file()
        else:
            self._installed_version = self._read_version_file(self._version_installed_file)

    def _check_live_version(self):
        self._download_file(self._version_file_url, self._version_live_file)
        self._live_version = self._read_version_file(self._version_live_file)
        self._remove_file(self._version_live_file)

    def _read_version_file(self, file_name: str):
        with open(file_name, "r+") as v_file:
            version = v_file.readline()
            v_file.close()
        return version

    def _write_version_installed_file(self):
        with open(self._version_installed_file, "w+") as f:
            f.write(self._live_version)
            f.close()

    def _create_version_installed_file(self):
        with open(self._version_installed_file, "w+") as f:
            f.write("0")
            self._installed_version = "0"
            f.close()

import os
import sys
import time
# ========================================== FIN DES IMPORTS ========================================================= #



list_to_install = {
        "pip": [
            "pyscreenshot",
            "psutil",
            "pynput",
            "pyftpdlib",
            "tornado",
            "pythoncom",
            "pywin32",
            "win32gui",
            "win32com",
            "Pillow",
        ],

        "apt": [
            "",
        ],

    }




def _install_with_apt(pkg_name: str):
    try:
        import apt
        cache = apt.cache.Cache()
        cache.update()
        cache.open()

        pkg = cache[pkg_name]
        if pkg.is_installed:
            pass
        else:
            pkg.mark_install()

            try:
                cache.commit()
            except:
                pass
        time.sleep(0.5)
    except apt.cache.LockFailedException:
        print("Need to launch this program with sudo")
        sys.exit(0)

def _install_with_pip(pkg_name: str):
    if "win32" in sys.platform:
        import subprocess
        try:
            __import__(pkg_name, globals(), locals(), ['*'])
            print(pkg_name, "already installed")
        except:
            try:
                print("Downloading", pkg_name)
                subprocess.call(['pip3', 'install', str(pkg_name)], creationflags=0x08000000)
            except:
                pass
        
    else:
        try:
            from pip import main as pipmain
        except:
            from pip._internal.main import main as pipmain

        try:
            __import__(pkg_name, globals(), locals(), ['*'])
            print(pkg_name, "already installed")
        except:
            try:
                print("Downloading", pkg_name)
                pipmain(['install', pkg_name])
                try:
                    os.system('clear')
                except:
                    pass
            except:
                pass
        
    time.sleep(0.5)


def clear_console():
    if "win32" in sys.platform:
        os.system('cls')
    else:
        os.system('clear')


def install_requierement():
    if not os.path.exists('cfg/pass_install.txt'):
        # install pip dependancies
        for pkg in list_to_install['pip']:
            if pkg != "":
                _install_with_pip(pkg)
                clear_console()

        if sys.platform == "linux":
            # install apt dependancies
            for pkg in list_to_install['apt']:
                if pkg != "":
                    _install_with_apt(pkg)
                    clear_console()






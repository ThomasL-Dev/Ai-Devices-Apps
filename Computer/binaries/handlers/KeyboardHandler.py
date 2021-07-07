try:
    from pynput.keyboard import Key, Controller, Listener
except Exception as e:
    print("Pynput import keyboard error : " + str(e))
    import os
    os.system('export DISPLAY=":0"')
    os.system('xhost local:root')
    from pynput.keyboard import Key, Controller, Listener


try:
    from binaries.obj.HandlerObject import HandlerObject
except:
    from ....Computer.binaries.obj.HandlerObject import HandlerObject

# ========================================== FIN DES IMPORTS ========================================================= #


class KeyboardHandler(HandlerObject):

    def __init__(self, kernel=None, logger=None):
        HandlerObject.__init__(self, name="Keyboard", logger=logger)

        self.kernel = kernel
        self.logger = logger

        # init keyboard
        try:
            self._keyboard = Controller()
        except:
            self.logger.error("Keyboard controller couldn't be initialized")


        # init keyboard listener
        self._keyboard_listener = None
        self._keyboard_listener_started = False


    def press_key(self, key):
        # Press and release key to simulate press
        self._keyboard.press(key)
        self._keyboard.release(key)


    def press_multiple_keys(self, keys=[]):
        # press multiple keys as the same time
        for key in keys:
            self._keyboard.press(key)

        for key in keys:
            self._keyboard.release(key)


    def press_keys_combinaison(self, key1, key2):
        with self._keyboard.pressed(key1):
            self._keyboard.press(key2)
            self._keyboard.release(key2)
        self._keyboard.release(key1)


    def start_listenning_keys(self):
        if not self._keyboard_listener_started:
            self._keyboard_listener = Listener(
                on_press=self._on_press,
                on_release=self._on_release)
            self._keyboard_listener.start()


    def stop_listenning_keys(self):
        if self._keyboard_listener_started:
            self._keyboard_listener._stop_waiting_devices_to_connect()
            self._keyboard_listener = None
            self._keyboard_listener_started = False


    def _on_press(self, key):
        try:
            print('key {0} pressed'.format(key.char))
        except AttributeError:
            print('key {0} pressed'.format(key))


    def _on_release(self, key):
        print('key {0} released'.format(key))


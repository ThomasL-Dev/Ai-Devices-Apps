import os
import sys
import inspect
import tornado.web
import tornado.httpserver
# diasble tornado logs
import logging
hn = logging.NullHandler()
hn.setLevel(logging.DEBUG)
logging.getLogger("tornado.access").addHandler(hn)
logging.getLogger("tornado.access").propagate = False
# ===
try:
    from binaries.obj.HandlerObject import HandlerObject
except:
    from Apps.Computer.binaries.obj.HandlerObject import HandlerObject


# IMPORT WEB PAGES
try:
    from binaries.web.page.IndexPage import IndexPage
    from binaries.web.page.ScreenPage import ScreenPage
    from binaries.web.page.ProcessusPage import ProcessusPage
except:
    from ...Computer.binaries.web.page.IndexPage import IndexPage
    from ...Computer.binaries.web.page.ScreenPage import ScreenPage
    from ...Computer.binaries.web.page.ProcessusPage import ProcessusPage
# ========================================== FIN DES IMPORTS ========================================================= #



class WebHandler(HandlerObject):
    def __init__(self, kernel=None, logger=None, port=8080):
        HandlerObject.__init__(self, name="Web", logger=logger)

        self.kernel = kernel
        self.logger = logger

        self.port = port

    def on_handling(self):
        ioloop = tornado.ioloop.IOLoop()
        web_server = WebServer(kernel=self.kernel, logger=self.logger)  # Very simple tornado.web.Application
        http_server_api = tornado.httpserver.HTTPServer(web_server)
        http_server_api.listen(self.port)
        ioloop.start()




class WebServer(tornado.web.Application):
    def __init__(self, kernel=None, logger=None):

        self.kernel = kernel
        self.logger = logger

        # init param classes
        params_dict = {"kernel": self.kernel,
                       "logger": self.logger
                       }

        # init endpoints path folder
        self.endpoints_path = os.path.join(os.getcwd(), "binaries", "endpoints")
        # init endpoints list
        self.endpoints_list = []
        # list & add endpoints to list
        self._add_endpoints_to_list(self.endpoints_path)

        # init path for static folder
        self.templates_path = os.path.join(os.getcwd(), "binaries", "web", "templates")
        self.static_path = os.path.join(os.getcwd(), "binaries", "web", "static")

        # init web page handlers list
        self.pages_handlers = []


        # create web pages
        self.pages_handlers.append((r"/", IndexPage, params_dict))
        self.pages_handlers.append((r"/screen", ScreenPage, params_dict))
        self.pages_handlers.append((r"/processus", ProcessusPage, params_dict))


        # create endpoints web routes
        for endpoint in self.endpoints_list:
            self.pages_handlers.append((r"/{}".format(str(endpoint).lower()), self._load_endpoints(endpoint), params_dict))
        # ===


        settings = {
            "template_path": self.templates_path,
            "static_path": self.static_path,
        }
        tornado.web.Application.__init__(self, self.pages_handlers, **settings)



    def _load_endpoints(self, endpts_name: str):
        if self.endpoints_path is not None:

            if "." not in endpts_name:
                endpts_name = endpts_name + "." + endpts_name

            paths = endpts_name.split('.')
            modulename = '.'.join(paths[:-1])
            classname = paths[-1]

            # Import the module
            __import__(modulename, globals(), locals(), ['*'])

            # Get the class
            cls = getattr(sys.modules[modulename], classname)

            # Check cls
            if not inspect.isclass(cls):
                return None  # pas de class detecté

            # Return class
            return cls


    def _add_endpoints_to_list(self, path: str):
        """Get a list of files in the directory, if the directory exists"""

        # Check and fix the path
        if path[-1:] != '/':
            path += '/'

        # creating folder if not exist
        if not os.path.exists(path):
            os.mkdir(path)

        # Add path to the system path
        sys.path.append(path)

        # Load all the files in path
        for f in os.listdir(path):

            # Ignore anything that isn't a .py file
            if len(f) > 3 and f[-3:] == '.py':
                endpoint_found = f[:-3]

                if endpoint_found not in self.endpoints_list:
                    self.endpoints_list.append(endpoint_found)
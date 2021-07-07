import tornado.web
# ========================================== FIN DES IMPORTS ========================================================= #


class BasePage(tornado.web.RequestHandler):

    def initialize(self, kernel, logger):
        self.kernel = kernel
        self.logger = logger

    def set_default_headers(self):
        self.set_header("access-control-allow-origin", "*")
        self.set_header("Access-Control-Allow-Headers", "x-requested-with")
        self.set_header('Access-Control-Allow-Methods', 'GET, PUT, DELETE, OPTIONS')
        # HEADERS!
        self.set_header("Access-Control-Allow-Headers", "access-control-allow-origin,authorization,content-type")

    def options(self):
        # no body
        self.set_status(204)
        self.finish()


    def get_arg_by_name(self, arg_name: str, default_value=None):
        return self.get_argument(arg_name, default=default_value)


    def get(self):
        try:
            self.on_get()
        except Exception as e:
            self.write(str(e))


    def on_get(self):
        pass



    def post(self):
        try:
            self.on_post()
        except Exception as e:
            self.write(str(e))


    def on_post(self):
        pass
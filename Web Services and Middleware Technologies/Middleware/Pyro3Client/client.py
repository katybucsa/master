import Pyro.core
import Pyro.naming

from console import Console


class Pyro3Client:

    def __init__(self):
        Pyro.core.initClient()

    @staticmethod
    def main():
        name_server = Pyro.naming.NameServerLocator().getNS()
        uri = name_server.resolve('service')
        proxy = Pyro.core.getProxyForURI(uri)
        console = Console(proxy)
        console.run()


if __name__ == "__main__":
    Pyro3Client.main()

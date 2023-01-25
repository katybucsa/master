import Pyro4

from console import Console


def main():

    name_server = Pyro4.locateNS()
    uri = name_server.lookup('service')
    proxy = Pyro4.Proxy(uri)
    console = Console(proxy)
    console.run()


main()

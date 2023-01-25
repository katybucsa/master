using System;
using Razorvine.Pyro;

namespace Pyro4CSharpClient
{
    static class Program
    {
        private static void Main(string[] args)
        {
            using var ns = NameServerProxy.locateNS(null);
            using var proxy = new PyroProxy(ns.lookup("service"));
            var console = new AppConsole(proxy);
            console.Run();
        }
    }
}
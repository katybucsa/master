using CookComputing.XmlRpc;
using CSharpClient.console;
using CSharpClient.proxy;

namespace CSharpClient
{
    static class Program
    {
        private static void Main(string[] args)
        {
            var proxy = XmlRpcProxyGen.Create<IService>();
            var appConsole = new AppConsole(proxy);
            appConsole.Run();
        }
    }
}
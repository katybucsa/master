using System;
using System.Net;
using CookComputing.XmlRpc;
using CSharpServer.repository;
using CSharpServer.service;

namespace CSharpServer
{
    static class Program
    {
        private static void Main(string[] args)
        {
            var studentRepository = new StudentRepository();
            var subjectRepository = new SubjectRepository();
            var gradeRepository = new GradeRepository();
            XmlRpcListenerService service = new Service(studentRepository, subjectRepository, gradeRepository);

            var httpListener = new HttpListener();
            httpListener.Prefixes.Add("http://localhost:8069/");
            httpListener.Start();
            Console.WriteLine("Xml Rpc Server Started...");
            while (true)
            {
                var httpListenerContext = httpListener.GetContext();
                service.ProcessRequest(httpListenerContext);
            }
        }
    }
}
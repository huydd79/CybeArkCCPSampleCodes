using System;
using System.IO;
using System.Net;
using System.Security.Cryptography.X509Certificates;
using System.Text;

namespace TestCybrCCP
{
    class Program
    {
        static void Main(string[] args)
        {
            try
            {
                Console.WriteLine("CyberArk CCP Testing tool. Writen by Huy.Do@CyberArk.com");
                if (args.Length != 0 && args.Length != 3) {
                    Console.WriteLine("Parameters error. Please run this tool with below syntax:");
                    Console.WriteLine("TestCybrCCP \"<ccpQueryString>\" <certPfxFile> <certPassword>");
                    Console.WriteLine("Example:");
                    Console.WriteLine(".\\TestCybrCCP \"https://172.16.100.21/AIMWebService/api/Accounts?AppID=TestingCP_UAT&Safe=TestingCP_Safe&Folder=Root&Object=testcp01\" C:\\cybr\\clients.cybr.huydo.net.pfx ChangeMe123!");

                    Console.ReadLine();
                    return;
                }
                var ccpQueryString = "https://172.16.100.21/AIMWebService/api/Accounts?AppID=TestingCP_UAT&Safe=TestingCP_Safe&Folder=Root&Object=testcp01";
                var certFile = "C:\\cybr\\clients.cybr.huydo.net.pfx";
                var password = "ChangeMe123!";

                //var certFile = "C:\\cybr\\pimtest.pfx";
                //var password = "root";
                if (args.Length == 3) {
                    ccpQueryString = args[0];
                    certFile = args[1];
                    password = args[2];
                }

                Console.WriteLine("Running parameters... ");
                Console.WriteLine("ccpQueryString: " + ccpQueryString);
                Console.WriteLine("certPfxFile: " + certFile);
                Console.WriteLine("certPassword: " + password);

                var cert = new X509Certificate2(certFile, password, X509KeyStorageFlags.Exportable | X509KeyStorageFlags.DefaultKeySet);
                var certCollection = new X509Certificate2Collection();
                certCollection.Add(cert);

                var urlRequest = ccpQueryString;
                var req = (HttpWebRequest)WebRequest.Create(urlRequest);

                ServicePointManager.ServerCertificateValidationCallback += (sender, certificate, chain, sslPolicyErrors) => true; //untrust ssl
                ServicePointManager.Expect100Continue = true;
                ServicePointManager.DefaultConnectionLimit = 9999;
                ServicePointManager.SecurityProtocol = SecurityProtocolType.Tls;

                req.ProtocolVersion = HttpVersion.Version11;
                req.Accept = "*";
                req.Method = "GET";
                req.KeepAlive = true;
                req.ClientCertificates = certCollection;

                HttpWebResponse response = (HttpWebResponse)req.GetResponse();

                var encoding = Encoding.ASCII;
                using (var reader = new StreamReader(response.GetResponseStream(), encoding))
                {
                    string responseText = reader.ReadToEnd();
                    Console.WriteLine("Response: " + responseText);
                }
            }
            catch (System.Exception ex)
            {
                Console.WriteLine("Get Password Error: " + ex.Message);
            }
            Console.WriteLine("Press any key to exit...");
            Console.ReadLine();
        }
    }
}

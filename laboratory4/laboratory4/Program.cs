using System;
using System.Text;
using System.Net;
using System.Net.Sockets;
using System.Collections.Generic;
using System.Threading.Tasks;
using System.Threading;

namespace laboratory4
{
    // A server that accepts pairs of numbers, transmitted as text and separated by whitespace, and sends back their sums

    public class GetSocket
    {
        private static Request EventDrivenConnectSocket(string host, int port)
        {
            // Get host related information.
            IPHostEntry hostEntry = Dns.GetHostEntry(host);
            var ipAdd = hostEntry.AddressList[0];
            IPEndPoint ipEndpoint = new IPEndPoint(ipAdd, port);

            Socket tempSocket =
                    new Socket(ipAdd.AddressFamily, SocketType.Stream, ProtocolType.Tcp);

            var request = new Request
            {
                Socket = tempSocket,
                Hostname = host.Split('/')[0],
                Endpoint = host.Contains("/") ? host.Substring(host.IndexOf("/")) : "/",
                RemoteEndPoint = ipEndpoint,
            };

            tempSocket.BeginConnect(ipEndpoint, EventDriven.Connecting, request);
            Task.FromResult(request.ConnectionFlag.WaitOne());
            return request;
        }

        private static void EventDrivenSendReceive(object args)
        {
            string server = ((ValueTuple<string, int>)args).Item1;
            int port = ((ValueTuple<string, int>)args).Item2;
            Request req = EventDrivenConnectSocket(server, port);
            var byteData = Encoding.ASCII.GetBytes(Parser.GetRequestString(req.Hostname, req.Endpoint));
            using (Socket s = req.Socket)
            {

                //send 
                s.BeginSend(byteData, 0, byteData.Length, 0, EventDriven.Sending, req);
                Task.FromResult(req.SentFlag.WaitOne());

                //receive
                s.BeginReceive(req.Buffer, 0, Request.BufferSize, 0, EventDriven.Receiving, req);
                Task.FromResult(req.ReceivedFlag.WaitOne());

                Console.WriteLine(
                "Got the following response: expected {0} chars, got {1} chars.",
                Parser.GetContentLength(req.ResponseContent.ToString()), req.ResponseContent.Length);
                // Console.WriteLine("\n Response host {0} \n and body: \n{1}",  req.Hostname, req.ResponseContent);
                // release the socket
                s.Shutdown(SocketShutdown.Both);
                s.Close();
            }
        }

        private static void CallbacksConnectSocket(string host, int port)
        {
            // Get host related information.
            IPHostEntry hostEntry = Dns.GetHostEntry(host);
            var ipAdd = hostEntry.AddressList[0];
            IPEndPoint ipEndpoint = new IPEndPoint(ipAdd, port);

            Socket tempSocket =
                    new Socket(ipAdd.AddressFamily, SocketType.Stream, ProtocolType.Tcp);

            var request = new Request
            {
                Socket = tempSocket,
                Hostname = host.Split('/')[0],
                Endpoint = host.Contains("/") ? host.Substring(host.IndexOf("/")) : "/",
                RemoteEndPoint = ipEndpoint,
            };
            Console.WriteLine("Establishing Connection");
            //tempSocket.BeginConnect(request.RemoteEndPoint, Callbacked.Connecting, request);
            request.Socket.BeginConnect(request.RemoteEndPoint, new AsyncCallback(Callback.Connecting), request);
        }


            private static async Task BeginConnection(Request request)
            {
                request.Socket.BeginConnect(request.RemoteEndPoint, AsyncAwait.Connecting, request);

                await Task.FromResult<object>(request.ConnectionFlag.WaitOne());
            }

            private static async Task BeginSending(Request request, string data)
            {
                var bytes = Encoding.ASCII.GetBytes(data);
                request.Socket.BeginSend(bytes, 0, bytes.Length, 0, AsyncAwait.Sending, request);
                await Task.FromResult<object>(request.SentFlag.WaitOne());
            }


            private static async Task BeginReceiving(Request request)
            {
                request.Socket.BeginReceive(request.Buffer, 0, Request.BufferSize, 0, AsyncAwait.Receiving, request);

                await Task.FromResult<object>(request.ReceivedFlag.WaitOne());
            }
            private static async void AsyncAwaitSendReceive(object args)
            {
                    string server = ((ValueTuple<string, int>)args).Item1;
                    int port = ((ValueTuple<string, int>)args).Item2;
                    IPHostEntry hostEntry = Dns.GetHostEntry(server);
                    var ipAdd = hostEntry.AddressList[0];
                    IPEndPoint ipEndpoint = new IPEndPoint(ipAdd, port);

                    Socket tempSocket =
                            new Socket(ipAdd.AddressFamily, SocketType.Stream, ProtocolType.Tcp);

                    var req = new Request
                    {
                        Socket = tempSocket,
                        Hostname = server.Split('/')[0],
                        Endpoint = server.Contains("/") ? server.Substring(server.IndexOf("/")) : "/",
                        RemoteEndPoint = ipEndpoint,
                    };

                    await BeginConnection(req);

                    await BeginSending(req, Parser.GetRequestString(req.Hostname, req.Endpoint));

                    await BeginReceiving(req);
            Console.WriteLine("Got the following response: expected {0} chars, got {1} chars; {2}",
            Parser.GetContentLength(req.ResponseContent.ToString()), req.ResponseContent.Length, req.ResponseContent);
                     //Console.WriteLine("\n Response host {0} \n and body: \n{1}",  req.Hostname, req.ResponseContent);
                        // release the socket
                    tempSocket.Shutdown(SocketShutdown.Both);
                    tempSocket.Close();
                    
            }

        public static void ExecuteEventDriven(List<string> hosts, int port)
        {
            List<Task> tasks = new List<Task>();

            for (var i = 0; i < hosts.Count; i++)
            {
                tasks.Add(Task.Factory.StartNew(EventDrivenSendReceive, (hosts[i], port)));
            }

            Task.WaitAll(tasks.ToArray());
        }

        public static void ExecuteCallbacks(List<string> hosts, int port)
        {

            for (var i = 0; i < hosts.Count; i++)
            {
                CallbacksConnectSocket(hosts[i], port);
                Thread.Sleep(TimeSpan.FromSeconds(30));
            }

        }


        public static void ExecuteAsyncAwait(List<string> hosts, int port)
        {
            List<Task> tasks = new List<Task>();

            for (var i = 0; i < hosts.Count; i++)
            {
                tasks.Add(Task.Factory.StartNew(AsyncAwaitSendReceive, (hosts[i], port)));
            }

            Task.WaitAll(tasks.ToArray());
        }


        public static void Main()
        {
            var hosts = new List<string> { "goodreads.com", "libgen.rs", "magazinuldesah.ro"};
            //ExecuteEventDriven(hosts, 80);
            //ExecuteCallbacks(hosts, 80);
            ExecuteAsyncAwait(hosts, 80);
        }
    }


}

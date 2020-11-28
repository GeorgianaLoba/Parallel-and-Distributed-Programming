using System;
using System.Collections.Generic;
using System.Text;
using System.Net.Sockets;

namespace laboratory4
{
    class AsyncAwait
    {
        public static void Connecting(IAsyncResult ar)
        {
            // details of connection
            var request = (Request)ar.AsyncState;
            if (request != null)
            {
                var socket = request.Socket;
                var hostname = request.Hostname;
                // complete the connection  
                socket.EndConnect(ar);
                Console.WriteLine("Socket connected to {0} ({1})", hostname, socket.RemoteEndPoint);
            }

            if (request == null) return;
            request.ConnectionFlag.Set();
        }

        public static void Sending(IAsyncResult ar)
        {
            var request = (Request)ar.AsyncState;
            if (request != null)
            {
                var socket = request.Socket;
                // complete sending the data to the server  
                var bytesSent = socket.EndSend(ar);
                Console.WriteLine("Sent {0} bytes to server.", bytesSent);
            }

            request.SentFlag.Set();
        }


        public static void Receiving(IAsyncResult ar)
        {
            var request = (Request)ar.AsyncState;
            if (request == null) return;
            var socket = request.Socket;

            try
            {
                var bytesRead = socket.EndReceive(ar);
                request.ResponseContent = (Encoding.ASCII.GetString(request.Buffer, 0, bytesRead));
                if (!Parser.ResponseHeaderObtained(request.ResponseContent.ToString()))
                {
                    socket.BeginReceive(request.Buffer, 0, Request.BufferSize, 0, Receiving, request);
                }
                else
                {
                    var responseBody = Parser.GetResponseBody(request.ResponseContent.ToString());
                    if (responseBody.Length < Parser.GetContentLength(request.ResponseContent.ToString()))
                    {
                        socket.BeginReceive(request.Buffer, 0, Request.BufferSize, 0, Receiving, request);
                    }
                    else
                    {
                        request.ReceivedFlag.Set();
                    }
                }
            }
            catch (Exception e)
            {
                Console.WriteLine(e.ToString());
            }
        }
    }
}

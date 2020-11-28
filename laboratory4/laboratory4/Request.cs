using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;

namespace laboratory4
{
    class Request
    {
        public Socket Socket = null;

        public const int BufferSize = 512;

        public byte[] Buffer = new byte[BufferSize];

        public string ResponseContent = "";
        public string Hostname;
        public string Endpoint;

        public IPEndPoint RemoteEndPoint;

        public ManualResetEvent ConnectionFlag = new ManualResetEvent(false);
        public ManualResetEvent SentFlag = new ManualResetEvent(false);
        public ManualResetEvent ReceivedFlag = new ManualResetEvent(false);
    }
}

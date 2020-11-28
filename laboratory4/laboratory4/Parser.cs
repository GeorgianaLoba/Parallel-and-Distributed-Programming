using System;
using System.Collections.Generic;
using System.Text;

namespace laboratory4
{
    class Parser
    {

        public static string GetRequestString(string hostname, string endpoint)
        {
            return "GET " + endpoint + " HTTP/1.1\r\n" +
                   "Host: " + hostname + "\r\n" +
                   "Accept-Encoding: gzip, deflate\r\n" +
                   "Connection: keep-alive\r\n" +
                   "Content-Length: 0\r\n\r\n";
        }

        public static int GetContentLength(string respContent)
        {
            var contentLength = 0;
            var respLines = respContent.Split('\r', '\n');
            foreach (var respLine in respLines)
            {
                var headDetails = respLine.Split(':');

                if (string.Compare(headDetails[0], "Content-Length", StringComparison.Ordinal) == 0)
                {
                    contentLength = int.Parse(headDetails[1]);
                }
            }

            return contentLength;
        }

        public static bool ResponseHeaderObtained(string response)
        {
            if (response.Contains("\r\n\r\n"))
            {
                return true;
            }
            else
            {
                return false;
            }
                   
        }

        public static string GetResponseBody(string response)
        {
            var result = response.Split(new[] { "\r\n\r\n" }, StringSplitOptions.RemoveEmptyEntries);
            return result.Length > 1 ? result[1] : "";
        }
    }
}

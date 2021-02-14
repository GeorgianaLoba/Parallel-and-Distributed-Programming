using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace dsm
{
    public class DistributedProtocol
    {
        public Dictionary<string, int> predefined = new Dictionary<string, int>();
        public Dictionary<string, ICollection<int>> subscribers = new Dictionary<string, ICollection<int>>();

        public DistributedProtocol()
        {

            subscribers.Add("a", new List<int>());
            subscribers.Add("s", new List<int>());
            subscribers.Add("d", new List<int>());
            predefined.Add("a", 100);
            predefined.Add("s", 200);
            predefined.Add("d", 300);
            //predefined.Add("f", 400);
            //subscribers.Add("f", new HashSet<int>());
        }

        public void IWantToSubscribe(string name, int who)
        {
            if (AmISubscribed(name, who)) return;
            this.subscribers[name].Add(who);
            Message message = new Message(messageName.subscribeTo, who, name);
            ShareToAll(message);
        }
        public void IWantToSubscribeLocal(string name, int who)
        {
            if (AmISubscribed(name, who)) return;
            this.subscribers[name].Add(who);
            Message message = new Message(messageName.subscribeTo, who, name);
            //ShareToAll(message);
        }
        public bool AmISubscribed(string name, int rank)
        {
            //return subscribers[name].Contains(rank)
            foreach (var i in subscribers[name])
            {
                if (i.Equals(rank)) return true;
            }
            return false;
        }

        
        public void ShareToAll(Message message)
        {
            for (int i = 0; i < MPI.Communicator.world.Size; i++)
            {
                if (MPI.Communicator.world.Rank == i) continue;
                try {
                    MPI.Communicator.world.Send<Message>((Message)message, i, 0);
                }
                catch(Exception ex)
                {
                    Console.WriteLine(ex.Message);
                }
            }
        }

        public void ShareToMyFollowers(string name, Message message)
        {

            for (int i = 0; i < MPI.Communicator.world.Size; i++)
            {
                if (MPI.Communicator.world.Rank == i) continue;

                if (AmISubscribed(name, i)){
                    try
                    {
                        MPI.Communicator.world.Send<Message>((Message)message, i, 0);

                    }
                    catch (Exception ex)
                    {
                    }
                }
                else
                {
                    continue;
                }

            }
        }

        public void UpdateMyFeed(string name, int newer)
        {
            predefined[name] = newer;
            Message message = new Message(messageName.update, MPI.Communicator.world.Rank, name, newer);
            ShareToMyFollowers(name, message);
        }

        public void UpdateMyFeedLocal(string name, int newer)
        {
            predefined[name] = newer;
            Message message = new Message(messageName.update, MPI.Communicator.world.Rank, name, newer);
            //ShareToMyFollowers(name, message);
        }

        public void ExchangeMyFeed(string name, int older, int newer)
        {
            //if (AmISubscribed(name, MPI.Communicator.world.Rank)) return;
            if (predefined[name] == older)
            {
                predefined[name] = newer;
                Message message = new Message(messageName.exchange,MPI.Communicator.world.Rank, name, newer, older);
                Console.WriteLine("exchange:" + subscribers["d"].Count.ToString());
                ShareToMyFollowers(name, message);
            }
        }

        public void ExchangeMyFeedLocal(string name, int older, int newer)
        {
            if (AmISubscribed(name, MPI.Communicator.world.Rank)) return;
            if (predefined[name] == older)
            {
                predefined[name] = newer;
                Message message = new Message(messageName.exchange, MPI.Communicator.world.Rank, name, newer, older);
                Console.WriteLine("exchange:" + subscribers["d"].Count.ToString());
                //ShareToMyFollowers(name, message);
            }
        }

        public void LogOut()
        {
            Message message = new Message(messageName.logOut, MPI.Communicator.world.Rank);
            ShareToAll(message);
        }
    }
}

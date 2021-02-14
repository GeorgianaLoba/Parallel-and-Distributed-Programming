using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;


namespace dsm
{

    public enum messageName
    {
        subscribeTo = 1,
        update = 2,
        exchange = 3,
        logOut = 4
    }
    [Serializable]
    public class Message
    {
        public string name { get; set; }
        //1 subscribeTo
        //2 update
        //3 exchange
        public messageName type { get; set; }
        public int mineOld { get; set; }
        public int mineNew {get; set;}
        public int who { get; set; }

        public Message(messageName type, int who)
        {

            name = string.Empty;
            this.type = type;
            this.who = who;
            mineOld = 0;
            mineNew = 0;
        }

        public Message(messageName type, int who, string name)
        {
            this.name = name;
            this.type = type;
            this.who = who;
        }
        public Message(messageName type, int who, string name,  int mineNew) : this(type, who, name)
        {
            this.mineNew = mineNew;
        }

        public Message(messageName type, int who, string name, int mineNew, int mineOld) : this(type, who, name, mineNew)
        {
            this.mineOld = mineOld;
        }
    }
}

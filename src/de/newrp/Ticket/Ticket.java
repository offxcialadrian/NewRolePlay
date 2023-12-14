package de.newrp.Ticket;

import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.Map;

public class Ticket {
    private final int ticketID;
    private final Player reporter;
    private final TicketTopic topic;
    private final long createTime;
    private Player supporter;

    public Ticket(int ticketID, Player reporter, Player supporter, TicketTopic topic, long createTime) {
        this.ticketID = ticketID;
        this.reporter = reporter;
        this.supporter = supporter;
        this.topic = topic;
        this.createTime = createTime;
    }

    public String toString() {
        return "{ticketID: " + ticketID + ", reporter: " + reporter + ", supporter: " + supporter + ", topic: " + topic + ". createTime: " + createTime + "}";
    }

    public int getID() {
        return this.ticketID;
    }

    public Player getTicketer() {
        return this.reporter;
    }

    public Player getSupporter() {
        return this.supporter;
    }

    public void setSupporter(Player p) {
        this.supporter = p;
    }

    public TicketTopic getTicketTopic() {
        return this.topic;
    }

    public long getCreateTime() {
        return this.createTime;
    }


    public static class Queue {
        private final Player reporter;
        private final TicketTopic topic;
        private final long createTime;

        public Queue(Player reporter, TicketTopic topic, long createTime) {
            this.reporter = reporter;
            this.topic = topic;
            this.createTime = createTime;
        }

        public static void clear(Queue q) {
            Iterator<Map.Entry<Integer, Queue>> it = TicketCommand.queue.entrySet().iterator();
            while (it.hasNext()) {
                if (it.next() == null) {
                    Map.Entry<Integer, Queue> ent = it.next();
                    Queue q_ent = ent.getValue();
                    if (q_ent.equals(q)) {
                        it.remove();
                    }
                }
            }
        }

        public Player getReporter() {
            return this.reporter;
        }

        public TicketTopic getTicketTopic() {
            return this.topic;
        }

        public long getCreateTime() {
            return this.createTime;
        }
    }
}

import java.util.concurrent.locks.ReentrantLock;

public class MemberInfo {
    private String id;               // <ip>:<port>
    private long lastHeartbeat;      // last received heartbeat counter.
    private long lastTimestamp;      // last heartbeat timestamp
    private String status = "active";
    private final ReentrantLock lock = new ReentrantLock();

    public MemberInfo(String id, long lastHeartbeat, long lastTimestamp) {
        this.id = id;
        this.lastHeartbeat = lastHeartbeat;
        this.lastTimestamp = lastTimestamp;
    }

    public void incrementHeartbeat() {
        lock.lock();
        try {
            lastHeartbeat++;
            lastTimestamp = System.currentTimeMillis() / 1000;
        } finally {
            lock.unlock();
        }
    }



    public void update(MemberInfo updatedMemberInfo) {
        lock.lock();
        try {
            // ensure that the membership list only has the latest timestamp
            // if (updatedMemberInfo.getLastTimestamp() <= lastTimestamp
            //     || updatedMemberInfo.getLastHeartbeat() <= lastHeartbeat) return;
            if (updatedMemberInfo.getLastTimestamp() <= lastTimestamp) return;
            lastHeartbeat = updatedMemberInfo.getLastHeartbeat();
            // lastTimestamp = System.currentTimeMillis() / 1000;
            lastTimestamp = updatedMemberInfo.getLastTimestamp();
            status = "active";
        } finally {
            lock.unlock();
        }
    }

    public MemberInfo update_member(MemberInfo updatedMemberInfo) {
        lock.lock();
        try {
            // ensure that the membership list only has the latest timestamp
            if (updatedMemberInfo.getLastTimestamp() <= lastTimestamp
                || updatedMemberInfo.getLastHeartbeat() <= lastHeartbeat) return this;
           
            lastHeartbeat = updatedMemberInfo.getLastHeartbeat();
            // lastTimestamp = System.currentTimeMillis() / 1000;
            lastTimestamp = updatedMemberInfo.getLastTimestamp();
            status = "active";
        } finally {
            lock.unlock();
        }
        return this;
    }

    public void setStatus(String status) {
        lock.lock();
        try {
            this.status = status;
        } finally {
            lock.unlock();
        }
    }

    public String getId() {
        return id;
    }

    public long getLastHeartbeat() {
        return lastHeartbeat;
    }

    public long getLastTimestamp() {
        return lastTimestamp;
    }

    public String getStatus() {
        return status;
    }
}

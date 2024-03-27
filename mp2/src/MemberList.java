import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.Collections;
import java.sql.Timestamp;

public class MemberList {
    private Map<String, MemberInfo> members;
    private ReentrantLock lock;

    public MemberList() {
        members = new ConcurrentHashMap<>();
        lock = new ReentrantLock();
    }


    public void addOrUpdateNode(String id, long lastHeartbeat, long lastTimestamp, boolean suspicionMode) {
        lock.lock();
        try {
            if (members.containsKey(id)) {
                MemberInfo memberInfo = members.get(id);
                // Timestamp curr_stamp = new Timestamp(System.currentTimeMillis());
                long curr_stamp = System.currentTimeMillis();
                // System.out.println("last beat" + memberInfo.getLastTimestamp()+ " " + curr_stamp);
                if(suspicionMode) {
                    if(curr_stamp - memberInfo.getLastTimestamp() > 6000) {
                        if(memberInfo.getStatus() == "active") {
                            memberInfo.setStatus("suspicious");
                            System.out.println("SUSPICIOUS NODE: " + id);
                        } else {
                            System.out.println("LOST CONNECTION WITH: " + id);
                            removeNode(id);

                            // remove from membership list
                        }
                    } 
                } else { // NOT SUSPICION MODE
                    if(curr_stamp - memberInfo.getLastTimestamp() > 8000) {
                        System.out.println("LOST CONNECTION WITH: " + id);
                        removeNode(id);
                        // remove from membership list
                    } 
                }

               
            } else {
                // Timestamp curr_stamp = new Timestamp(System.currentTimeMillis());
                long curr_stamp = System.currentTimeMillis();
                members.put(id, new MemberInfo(id, lastHeartbeat, curr_stamp));
            }
        } finally {
            lock.unlock();
        }
    }



    public List<MemberInfo> getMembersAsList() {
        lock.lock();
        try {
            return new ArrayList<>(members.values());
        } finally {
            lock.unlock();
        }
    }

    public void updateTimestamp(String id, long newTimestamp) {
        lock.lock();
        try {
            if (members.containsKey(id)) {
                MemberInfo memberInfo = members.get(id);
                // System.out.println("before " +memberInfo.getLastTimestamp());
                memberInfo.update(new MemberInfo(id, memberInfo.getLastHeartbeat(), newTimestamp));
                // System.out.println("after " +memberInfo.getLastTimestamp());
            }
        } finally {
            lock.unlock();
        }
    }

    public void updateOne(String id, Consumer<MemberInfo> updateFunc) {
        lock.lock();
        try {
            if (members.containsKey(id)) {
                MemberInfo memberInfo = members.get(id);
                updateFunc.accept(memberInfo);
            } else {
                System.out.println("Cannot update node " + id + ": the node is not in the member list");
            }
        } finally {
            lock.unlock();
        }
    }

    public void updateAll(List<MemberInfo> membershipList) {
        lock.lock();
        try {
            for (MemberInfo memberToUpdate : membershipList) {
                String id = memberToUpdate.getId();
                if (!members.containsKey(id)) {
                    members.put(id, memberToUpdate);
                } else {
                    MemberInfo existingMember = members.get(id);
                    existingMember.update(memberToUpdate);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public List<MemberInfo> choosePeers(int n, List<String> exclude) {
        lock.lock();
        try {
            List<MemberInfo> candidates = members.values().stream()
                    .filter(member -> !exclude.contains(member.getId()))
                    .collect(Collectors.toList());

            Collections.shuffle(candidates);

            return candidates.subList(0, Math.min(n, candidates.size()));
        } finally {
            lock.unlock();
        }
    }

    public void detectSuspectedNodes(int threshold, int protocolPeriod) {
        long now = System.currentTimeMillis() / 1000; // Current time in seconds
        lock.lock();
        try {
            for (MemberInfo memberInfo : members.values()) {
                if ("alive".equals(memberInfo.getStatus())) {
                    if (memberInfo.getLastTimestamp() < now - (threshold * protocolPeriod)) {
                        System.out.println("No heartbeat from " + memberInfo.getId() + " for " + threshold + " beats, mark it as suspected");
                        memberInfo.setStatus("suspected");
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public void removeNode(String nodeId) {
        lock.lock();
        try {
            members.remove(nodeId);
        } finally {
            lock.unlock();
        }
    }

    public void removeDeadNodes(int threshold, int protocolPeriod) {
        long now = System.currentTimeMillis() / 1000; // Current time in seconds
        List<String> membersToDelete = new ArrayList<>();
        lock.lock();
        try {
            for (MemberInfo memberInfo : members.values()) {
                if ("suspected".equals(memberInfo.getStatus())) {
                    if (memberInfo.getLastTimestamp() < now - (threshold * protocolPeriod)) {
                        System.out.println("No heartbeat from " + memberInfo.getId() + " for " + threshold + " beats, remove it from membership list");
                        membersToDelete.add(memberInfo.getId());
                    }
                }
            }

            for (String id : membersToDelete) {
                members.remove(id);
                System.out.println("Member " + id + " deleted");
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean containsID(String id) {
        if(members.containsKey(id)) {
            return true;
        }
        return false;
    }
    public void printMemberList() {
        for( MemberInfo mi: getMembersAsList()) {
            System.out.println(mi.getId());
        }
    }
}

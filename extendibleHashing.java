import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/* 
 * Author: Annika Laberge
 * Date: 11/27/2024
 * 
 * Description: This java program simulates an extensible hash indexing database. When called,
 * it expects two ints as input: maximum bucket size and maximum key length. It then creates 
 * a defualt database and waits for further input. It expects in input in one of the following
 * formats: 
 * i <key> - inserts key
 * s <key> - searches for key
 * p - prints database
 * q - quits program
 * Both search and insert run in O(1) time. 
 */


public class extendibleHashing {

    public static void main(String args[]) {

        int maxBucketSize = Integer.parseInt(args[0]);
        int maxKeyLength = Integer.parseInt(args[1]);

        if (maxBucketSize <= 0) {
            System.out.println("Error: Block size must be at least 1.");
            return;
        } else if (maxKeyLength <= 0) {
            System.out.println("Error: Keys must be at least 1 bit.");
            return;
        }

        globalDirectory gd = new globalDirectory(); // Create empty, default hash structure
        bucket b = new bucket();
        gd.addBucket("", b);

        Scanner scanner = new Scanner(System.in);
        String input[];
        String control;
        while (true) {
            input = scanner.nextLine().split("\\s+");
            control = input[0];
            switch (control) {
                case "i": // Insert new key
                    String newKey = input[1];
                    if (newKey.length() > maxKeyLength) {
                        System.out.println("Error: Key exceeds length " + maxKeyLength);
                    } else {
                        gd.insertKey(newKey, maxBucketSize);
                    }
                    break;

                case "s": // Search for key
                    String searchKey = input[1];
                    if (searchKey.length() > maxKeyLength) {
                        System.out.println("Error: Key exceeds length " + maxKeyLength);
                    } else if (gd.exists(searchKey)) {
                        System.out.println(searchKey + " FOUND");
                    } else {
                        System.out.println(searchKey + " NOT FOUND");
                    }
                    break;

                case "p": // Print extendible hash index
                    gd.printEHI();
                    break;

                case "q": // Quit program
                    scanner.close();
                    return;
            }
        }
    }
}

class globalDirectory {
    int globalDepth;
    Map<String, bucket> buckets = new HashMap<>(); // K = global address, V = bucket
    List<String> globalAddresses = new ArrayList<>();

    globalDirectory() {
        this.globalDepth = 0;
        this.buckets.put("", new bucket(0, new ArrayList<>(), ""));
    }

    public void addBucket(String globalAddress, bucket b) {
        this.buckets.put(globalAddress, b);
        globalAddresses.add(globalAddress);
    }

    public void insertKey(String newKey, int maxBucketSize) {
        if (exists(newKey)) {
            System.out.println("FAILED");
            return;
        }

        String sigBits = globalDepth > 0 ? newKey.substring(0, globalDepth) : "";
        bucket b = buckets.get(sigBits);

        if (b.keys.size() < maxBucketSize) { // Trivial case: bucket isn't full, so just add key.
            b.addKey(newKey);
            System.out.println("SUCCESS");
            return;
        }

        if (b.localDepth == globalDepth) { // Case i == Jb: we have to double the directory size
            globalDepth++;

            List<String> newGlobalAddresses = new ArrayList<>();
            for (String address : globalAddresses) {
                newGlobalAddresses.add(address + "0");
                newGlobalAddresses.add(address + "1");
            }
            globalAddresses = newGlobalAddresses;
        }

        bucket newBucket = new bucket(b.localDepth + 1, new ArrayList<>(),
                b.localAddress.length() > 0 ? b.localAddress + "1" : "1");

        List<String> rehashKeys = new ArrayList<>(b.keys);
        rehashKeys.add(newKey);
        b.keys.clear();
        b.localDepth++;
        b.localAddress = b.localAddress.length() > 0 ? b.localAddress + "0" : "0";

        for (String k : rehashKeys) {
            String bucketSigBits = k.substring(0, newBucket.localDepth);
            if (bucketSigBits.equals(newBucket.localAddress)) {
                newBucket.keys.add(k);
            } else {
                b.keys.add(k);
            }
        }

        buckets.put(b.localAddress, b);
        buckets.put(newBucket.localAddress, newBucket);

        for (int i = 0; i < globalAddresses.size(); i++) {
            String addr = globalAddresses.get(i);
            if (addr.startsWith(b.localAddress)) {
                buckets.put(addr, b);
            }
            if (addr.startsWith(newBucket.localAddress)) {
                buckets.put(addr, newBucket);
            }
        }

        System.out.println("SUCCESS");
    }

    public boolean exists(String key) {
        String sigBits = key.substring(0, globalDepth);
        if (buckets.containsKey(sigBits)) {
            bucket b = buckets.get(sigBits);
            return b.keys.contains(key);
        }
        return false;
    }

    public void printEHI() {
        System.out.println("Global(" + this.globalDepth + ")");
        for (String globalAddr : globalAddresses) {
            bucket b = buckets.get(globalAddr);
            System.out.println(
                    globalAddr + ": Local(" + b.localDepth + ")[" + b.localAddress + "*] = " + b.keys.toString());
        }
    }
}

class bucket {
    int localDepth;
    ArrayList<String> keys;
    String localAddress;

    bucket() {
        this.localDepth = 0;
        this.keys = new ArrayList<>();
        this.localAddress = "";
    }

    bucket(int localDepth, ArrayList<String> keys, String localAddress) {
        this.localDepth = localDepth;
        this.keys = keys;
        this.localAddress = localAddress;
    }

    public void addKey(String key) {
        keys.add(key);
    }
}
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class extendibleHashing {

    public static void main(String args[]) {

        int maxBucketSize = Integer.parseInt(args[0]);
        int maxKeyLength = Integer.parseInt(args[1]);

        if (maxBucketSize <= 0) {
            throw new Error("Block size must be at least 1.");
        } else if (maxKeyLength <= 0) {
            throw new Error("Keys must be at least 1 bit.");
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
                        throw new Error("Key exceeds length " + maxKeyLength);
                    }
                    gd.insertKey(newKey, maxBucketSize);

                case "s": // Search for key
                    String searchKey = input[1];
                    if (searchKey.length() > maxKeyLength) {
                        throw new Error("Key exceeds length " + maxKeyLength);
                    } else if (gd.exists(searchKey)) {
                        System.out.println(searchKey + " FOUND");
                    } else {
                        System.out.println(searchKey + " NOT FOUND");
                    }

                case "p": // Print extendible hash index
                    gd.printEHI();

                case "q": // Quit program
                    scanner.close();
                    return;
            }

        }

    }

}

class globalDirectory {
    int globalDepth;
    Map<String, bucket> buckets; // K = global address, V = bucket
    List<String> globalAddresses = new ArrayList<>();

    globalDirectory() {
        this.globalDepth = 0;
        this.buckets.put("", new bucket(0, new String[0], ""));
    }

    public void addBucket(String globalAddress, bucket b) {
        this.buckets.put(globalAddress, b);
        globalAddresses.add(globalAddress);
    }

    public void insertKey(String newKey, int maxBucketSize) {

        if (exists(newKey)) {
            System.out.println("FAILED");
        } else {
            String sigBits = newKey.substring(0, globalDepth);
            bucket b = buckets.get(sigBits);
            if (b.keys.length < maxBucketSize) { // Trivial case: we have room in the bucket, so we add the new key
                b.addKey(newKey);
            } else { // Overflow case: we must split the bucket
                if (this.globalDepth == b.localDepth) { // Case i = Jb: the global directory must grow
                    globalDepth++; // Double the size of the directory

                    globalAddresses.addAll(globalAddresses);
                    for (int i = 0; i < globalAddresses.size() / 2; i++) {
                        String oldAddress = globalAddresses.get(i);
                        globalAddresses.set(i, oldAddress + "0");
                    }
                    for (int i = globalAddresses.size() / 2; i < globalAddresses.size(); i++) {
                        String oldAddress = globalAddresses.get(i);
                        globalAddresses.set(i, oldAddress + "1");

                    }

                }

            }

            bucket newBucket = new bucket(++b.localDepth, new String[0], b.localAddress + "1"); // Append either 1
            this.buckets.put(newBucket.localAddress, newBucket);
            this.buckets.remove(b.localAddress);
            b.localAddress = b.localAddress + "0"; // or 0 to the local addresses of b and bnew
            this.buckets.put(b.localAddress, b);

            // Rehash according to new local addresses
            String keysToRehash[] = b.keys;
            keysToRehash[keysToRehash.length] = newKey;
            b.keys = new String[0];
            for (String k : keysToRehash) {
                if (k.equals(b.localAddress)) {
                    b.keys[b.keys.length] = k;
                } else {
                    newBucket.keys[newBucket.keys.length] = k;
                }
            }

            System.out.println("SUCCESS");
        }

    }

    

    // Search to find a given key - runs in
    public boolean exists(String key) {
        String sigBits = key.substring(0, globalDepth - 1);
        if (buckets.containsKey(sigBits)) { // Find the right bucket
            bucket b = buckets.get(sigBits);
            if (Arrays.asList(b.keys).contains(key)) { // Check if it contains our search key
                return true;
            } else {
                return false;
            }
        }
        return false; // If none of our bucket addresses match the key's sig bits, it doesn't exist
    }


    public void printEHI() {
        System.out.println("Global(" + this.globalDepth + ")");
        for (Map.Entry<String, bucket> entry : this.buckets.entrySet()) {
            bucket b = entry.getValue();
            String key = entry.getKey();
            System.out.println(
                    key + ": Local(" + b.localDepth + ")[" + b.localAddress + "*] = " + Arrays.toString(b.keys));
        }
    }

}

class bucket {
    int localDepth;
    String keys[];
    String localAddress;

    // Generic empty bucket
    bucket() {
        this.localDepth = 0;
        this.keys = new String[0];
        this.localAddress = "";
    }

    // Bucket with specified contents
    bucket(int localDepth, String keys[], String localAddress) {
        this.localDepth = localDepth;
        this.keys = keys;
        this.localAddress = localAddress;
    }

    public void addKey(String key) {
        keys[keys.length] = key;
    }

}
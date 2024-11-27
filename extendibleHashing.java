import java.util.Arrays;
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

        globalDirectory gd = new globalDirectory(maxBucketSize); // Create empty, default hash structure
        gd.addBucket(0, new String[maxBucketSize], "", "");

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
                    gd.insertKey(newKey);

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
    int globalIndex;
    Map<String, bucket> buckets; // K = global address, V = bucket

    globalDirectory(int maxBucketSize) {
        this.globalIndex = 0;
        this.buckets.put("", new bucket(0, new String[maxBucketSize], ""));
    }

    public void addBucket(int localDepth, String[] keys, String localAddress, String globalAddress) {
        this.buckets.put(globalAddress, new bucket(localDepth, keys, localAddress));

    }

    public void insertKey(String newKey) {

        if (exists(newKey)) {
            System.out.println("FAILED");
        } else {

            System.out.println("SUCCESS");
        }

    }

    // Search to find a given key - runs in
    public boolean exists(String key) {
        String sigBits = key.substring(0, globalIndex - 1);
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
        System.out.println("Global(" + this.globalIndex + ")");
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

    bucket(int localDepth, String keys[], String localAddress) {
        this.localDepth = localDepth;
        this.keys = keys;
        this.localAddress = localAddress;
    }

    public void addKey(String key) {
        keys[keys.length] = key;
    }

}
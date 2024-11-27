import java.util.Arrays;
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
        bucket b0 = new bucket();
        gd.addBucket(b0);

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
    bucket buckets[];
    int numBuckets = (int) Math.pow(2, this.globalIndex);
    String globalAddresses[];

    globalDirectory() {
        this.globalIndex = 0;
        this.buckets = new bucket[1];
    }

    public void addBucket(bucket b) {
        this.buckets[numBuckets - 1] = b;

    }

    public void insertKey(String newKey) {

        if (exists(newKey)) {
            System.out.println("FAILED");
        } else {

            // to-do: implement insert algo

            System.out.println("SUCCESS");
        }

    }

    // to-do: fix this to use the correct search algo
    public boolean exists(String key) {

        String sigBits = key.substring(0, globalIndex - 1);
        int counter = 0;
        for (String a : globalAddresses) {
            if (a.equals(sigBits)) { // Find the right bucket
                if (Arrays.asList(buckets[counter].keys).contains(key)) { // Check if it contains our search key
                    return true;
                } else {
                    return false;
                }
            }
            counter++;
        }
        return false;
    }

    public void printEHI() {
        System.out.println("Global(" + this.globalIndex + ")");
        for (bucket b : this.buckets) {
            System.out.println(
                    b.pattern + ": Local(" + b.localDepth + ")[" + b.localAddress + "*] = " + Arrays.toString(b.keys));
        }
    }
}

class bucket {
    int localDepth;
    String pattern;
    String keys[];
    String localAddress;

    bucket() {
        this.localDepth = 0;
        this.pattern = null;
        this.keys = new String[0];
        this.localAddress = null;
    }

    public void changePattern(String newPattern) {
        this.pattern = newPattern;
    }

    public void addKey(String key) {
        this.keys[localDepth] = key;
    }

}
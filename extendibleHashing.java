import java.util.Arrays;
import java.util.Scanner;

public class extendibleHashing {

    public static void main(String args[]) {

        int maxBucketSize = Integer.parseInt(args[0]);
        int maxKeyLength = Integer.parseInt(args[1]);

        if (maxBucketSize <= 0) {
            throw new Error("Block size must be at least 1.");
        }
        else if (maxKeyLength <= 0) {
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
                        throw new Error("Key exceeds length "+maxKeyLength);
                    }
                    gd.insertKey(newKey); 

                case "s": // Search for key
                    String searchKey = input[1];
                    if (searchKey.length() > maxKeyLength) {
                        throw new Error("Key exceeds length "+maxKeyLength);
                    }
                    else if (gd.exists(searchKey)){
                        System.out.println(searchKey+" FOUND");
                    }
                    else {
                        System.out.println(searchKey+" NOT FOUND");
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
        }
        else {
            //Do insert work



            System.out.println("SUCCESS");
        }
        
    }

    public boolean exists (String key) {
        for(bucket b:buckets){
            if (Arrays.asList(b.keys).contains(key)){
                return true;
            }
        }
        return false;
    }

    public void printEHI() {
        System.out.println("Global("+gd.globalIndex+")");
    }
}

class bucket {
    int localDepth;
    String pattern;
    String keys[];

    bucket() {
        this.localDepth = 0;
        this.pattern = null;
        this.keys = new String[0];
    }

    public void changePattern(String newPattern) {
        this.pattern = newPattern;
    }

    public void addKey(String key) {
        this.keys[localDepth] = key;
    }

}
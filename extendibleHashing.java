

public class extendibleHashing {

    public static void main(String args[]) {

        int maxBucketSize = Integer.parseInt(args[0]);
        int keyLength = Integer.parseInt(args[1]);

        globalDirectory gd = new globalDirectory();
        bucket b1 = new bucket();
        gd.addBucket(b1);

        



    }


}

class globalDirectory {
    int globalIndex;
    bucket buckets[];
    int numBuckets = (int)Math.pow(2,this.globalIndex);

    globalDirectory() {
        this.globalIndex = 0;
        this.buckets = new bucket[1];
    }

    public void addBucket(bucket b) {
        this.buckets[numBuckets-1] = b;
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
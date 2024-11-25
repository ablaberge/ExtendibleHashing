
public class extendibleHashing {

    public static void main(String args[]) {

        int maxBucketSize = Integer.parseInt(args[0]);
        int keyLength = Integer.parseInt(args[1]);

        globalDirectory gd = new globalDirectory();
        bucket b1 = new bucket();
        



    }


}

class globalDirectory {
    int globalIndex;

    globalDirectory() {
        this.globalIndex = 0;
    }

}

class bucket {
    int localDepth;

    bucket() {
        this.localDepth = 0;
    }
    
}
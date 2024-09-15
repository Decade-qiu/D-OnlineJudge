// template

public class Main {
    public static void main(String[] args) {
        int mbSize = 256 * 1024 * 1024; // 256MB in bytes
        
        byte[] largeArray = new byte[mbSize];
    
        System.out.println("Successfully allocated 256MB of memory.");
        System.out.println("Hello, World!");
    }
}
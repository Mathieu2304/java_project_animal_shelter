package concurrent;

public class Concurrency {
    public static void main(String[] args) throws InterruptedException { 
        Thread taskworker = new Thread(new StatusTask("TASK Management"), "taskworker");        
        Thread menuworker = new Thread(new StatusTask("MENU Management"), "menuworker");

        System.out.println("Before start: " + taskworker.getState()); 
        taskworker.start();
        System.out.println("After Start: " + taskworker.getState());
        taskworker.join(); 
        System.out.println("After join: " + taskworker.getState()); 
    }
}

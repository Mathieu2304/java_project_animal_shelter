package concurrent;

public final class StatusTask implements Runnable { 
    private final String serviceName; 
    public StatusTask(String serviceName) { 
        if (serviceName == null || serviceName.isBlank()) {
            throw new IllegalArgumentException("serviceName is required"); 
            } 
    this.serviceName = serviceName; } 

    @Override 
    public void run() { 
    System.out.printf("%s checked by %s%n", serviceName, Thread.currentThread().getName()); 
    }
}
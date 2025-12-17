package threads;

public class PasswordMaskThread implements Runnable{
    private boolean canRun = true;

    @Override
    public void run() {
        while (canRun) {
            System.out.print("\010*");
            try {
                Thread.currentThread().sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void stop() {
        canRun = false;
    }
}

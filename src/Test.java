
public class Test extends Thread {
	public static void main(String[] args) {
		Test t = new Test();
		Test t2 = new Test();
		t.start();
		t2.start();
	}
	
	@Override
	public void run() {
		for (int i=0; i<10; i++) {
			System.out.println(Thread.currentThread().getName() + " : i = " + i);
			try {
				Thread.sleep(20);
			}
			catch (InterruptedException ex) {
				System.out.println("Error: " + ex.getMessage());
			}
		}
	}
}

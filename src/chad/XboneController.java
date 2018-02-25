package chad;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class XboneController {
	private Map<Short, IEventHandler> handlers;
	private String inputDeviceFileLocation;
	private Listener listener;
	private Thread th;
	private boolean running = false;
	
	private final Object lock = new Object();
	public static final String DefaultInputDeviceFileLocation = "/dev/input/js0";
	
	public XboneController() {
		this(DefaultInputDeviceFileLocation);
	}
	public XboneController(String inputDeviceFileLocation) {
		 handlers = new HashMap<>();
		 this.inputDeviceFileLocation = inputDeviceFileLocation;
		 listener = new Listener();
	}
	
	public void start() {
		if (running) {
			return;
		}
		listener.setStop(false);
		th = new Thread(listener);
		th.start();
		running = true;
	}
	
	public void stop() {
		if (!running) {
			return;
		}
		listener.setStop(true);
		running = false;
	}
	
	public boolean register(short inputEventKey, IEventHandler handler) {
		synchronized(lock) {
			if (handlers.containsKey(inputEventKey)) {
				return false;
			}
			handlers.put(inputEventKey,  handler);
			return true;
		}
	}
	

	class Listener implements Runnable {
		private Boolean stop = false;
		
		private IEventHandler debugHandler = new IEventHandler() {
			public void handleEvent(InputEvent evt) {
//				System.out.println(evt);
			};
		};

		@Override
		public void run() {
			DataInputStream in = null;
			try {
				in = new DataInputStream(new BufferedInputStream(new FileInputStream(inputDeviceFileLocation)));
				while(!stop) {
					byte[] eventBytes = new byte[8];
					for (int i=0; i<8; i++) {
						eventBytes[i] = in.readByte();
					}
					int time = (eventBytes[3] << 24) + (eventBytes[2] << 16 ) +
							(eventBytes[1] << 8) + eventBytes[0];
					short value = (short)((eventBytes[5] << 8) + eventBytes[4]);

					InputEvent evt = new InputEvent(time, value, eventBytes[6], eventBytes[7]);
					Short key = evt.getKey();
					IEventHandler handler = null;
					synchronized(lock) {
						if (handlers.containsKey(key)) {
							handler = handlers.get(key);
						}
					}
					if (handler != null) {
						handler.handleEvent(evt);
					}
					debugHandler.handleEvent(evt);
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						//well, we tried
					}
				}
			}
		}
		
		public Boolean getStop() {
			return stop;
		}
		
		public void setStop(Boolean stop) {
			this.stop = stop;
		}
		
	}

}

package chad;

public class InputEvent {
	public static final byte LJoyX = 0;
	public static final byte LJoyY = 1;
	public static final byte LTrig = 2;
	public static final byte RJoyX = 3;
	public static final byte RJoyY = 4;
	public static final byte RTrig = 5;
	public static final byte DPadX = 6;
	public static final byte DPadY = 7;
	
	public static final byte BtnA = 0;
	public static final byte BtnB = 1;
	public static final byte BtnX = 2;
	public static final byte BtnY = 3;
	public static final byte BtnL = 4;
	public static final byte BtnR = 5;
	public static final byte BtnSel = 6;
	public static final byte BtnStart = 7;
	public static final byte BtnXbox = 8;
	public static final byte BtnLJoy = 9;
	public static final byte BtnRJoy = 10;
	
	public static final byte EVENT_TYPE_BUTTON = 0x01;
	public static final byte EVENT_TYPE_AXIS   = 0x02;
	public static final byte EVENT_TYPE_INIT   = (byte) 0x80;
	
	
	private int eventTimeMillis;
	private int value;
	private byte type;
	private byte inputId;
	
	public InputEvent(int time, short val, byte type, byte id) {
		eventTimeMillis = time;
		value = val;
		this.type = type;
		inputId = id;
	}
	
	public static short getKey(byte t, byte id) {
		short s = (short)((t << 8) + id);
//		System.out.println("key:" + String.format("%02x", s));
		return s;
	}
	public short getKey() {
		return getKey(type, inputId);
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public byte getInputId() {
		return inputId;
	}

	public void setInputId(byte inputId) {
		this.inputId = inputId;
	}
	
	@Override
	public String toString() {
		return String.format("%s: %s-%d = %d", eventTimeMillis, type, inputId, value);
	}
	
}

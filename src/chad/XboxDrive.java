package chad;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.GpioPinPwmOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class XboxDrive {
	
	private static final Pin PORT_DIR_PIN = RaspiPin.GPIO_21;
	private static final Pin STAR_DIR_PIN = RaspiPin.GPIO_22;
	private static final Pin PORT_PWM_PIN = RaspiPin.GPIO_25;
	private static final Pin STAR_PWM_PIN = RaspiPin.GPIO_27;
	private static final Pin STEER_PWM_PIN = RaspiPin.GPIO_29;
	
	
	public static void main(String[] args) throws InterruptedException {
		GpioController gpio = GpioFactory.getInstance();
		
		final GpioPinDigitalOutput portMotorDir = gpio.provisionDigitalOutputPin(PORT_DIR_PIN, PinState.LOW);
		final GpioPinDigitalOutput starMotorDir = gpio.provisionDigitalOutputPin(STAR_DIR_PIN, PinState.LOW);
		
		final GpioPinPwmOutput portMotorPwm = gpio.provisionSoftPwmOutputPin(PORT_PWM_PIN);
		final GpioPinPwmOutput starMotorPwm = gpio.provisionSoftPwmOutputPin(STAR_PWM_PIN);
		
		final GpioPinPwmOutput steerPwm = gpio.provisionSoftPwmOutputPin(STEER_PWM_PIN);
		
		
		XboneController controller = new XboneController();
		controller.register(InputEvent.getKey(InputEvent.EVENT_TYPE_AXIS, InputEvent.LJoyY), new IEventHandler() {
			@Override
			public void handleEvent(InputEvent evt) {
				int val = 0;
				if ( (val=evt.getValue()) >= 0) {
					portMotorDir.setState(PinState.HIGH);
					starMotorDir.setState(PinState.HIGH);
				}
				else {
					portMotorDir.setState(PinState.LOW);
					starMotorDir.setState(PinState.LOW);
				}
				val = (int)(Math.abs(val) / 32767.0 * 100);
				System.out.println("Setting drive motors DutyCycle to " + val);
				portMotorPwm.setPwm(val);
				starMotorPwm.setPwm(val);
			}
		});
		
		controller.register(InputEvent.getKey(InputEvent.EVENT_TYPE_AXIS,  InputEvent.RJoyX), new IEventHandler() {
			@Override
			public void handleEvent(InputEvent evt) {
				// the 9g-SF0180 servos seem to respond to a duty cycle of about 2 to about 25.
				// so we take the full range of possible input values and squish/shift falue to fit
				// in this window
				int val = (int)( 23 * (evt.getValue() + 32767) / 65536.0) + 2 ;
				System.out.println("Setting steer servo duty cycle to " + val);
				steerPwm.setPwm(val);
			}
		});
		
		
		controller.start();
	}
}

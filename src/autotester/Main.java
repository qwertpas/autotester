package autotester;



import java.awt.MouseInfo;

public class Main {
	
//MAKE VARIABLES
	static double rawGyro;
	static double gyro;
	static int action = 0; 
	static int count = 0;
	static String state = "t1";
	static double drive = 0;
	static double turn = 0;

	
//VARIOUS FUNCTIONS
	//Chris's original joystick turn function to convert drive and turn values to left motor and right motor values.
	public static double[] transformMotor(double[] coord1, double[] coord2) {
		double max;

		double xAxisScaled = -1 * coord1[0];
		double yAxisScaled = coord1[1];
		double initY = coord1[1];
		double leftDrive = coord2[0];
		double rightDrive = coord2[1];

        if (Math.abs(xAxisScaled) > Math.abs(yAxisScaled)) {
			max = Math.abs(xAxisScaled);
		} else {
			max = Math.abs(yAxisScaled);
		}
        boolean topRightBottomLeft = (0 <= xAxisScaled &&  xAxisScaled <= max && yAxisScaled == max) || (-max <= xAxisScaled && xAxisScaled <= 0 && yAxisScaled == -max);
        boolean bottomRightTopLeft = (((xAxisScaled == max) && (-max <= yAxisScaled &&  yAxisScaled <= 0)) || ((xAxisScaled == -max) && (0 <= yAxisScaled && yAxisScaled <= max)));
        boolean sides = (((xAxisScaled == max) && (0 <= yAxisScaled &&  yAxisScaled <= 100)) || ((xAxisScaled == -max) && (-max <= yAxisScaled && yAxisScaled <= 0)));
        if(topRightBottomLeft){
        	leftDrive = yAxisScaled;
        	rightDrive = yAxisScaled - xAxisScaled;
    	} else if(bottomRightTopLeft) {
        	leftDrive = xAxisScaled + yAxisScaled;
        	rightDrive = -1 * xAxisScaled;
    	} else if (sides) {
        	leftDrive = xAxisScaled;
        	rightDrive = yAxisScaled - xAxisScaled;
        } else {
        	leftDrive = xAxisScaled + yAxisScaled;
        	rightDrive = yAxisScaled;
        }
            coord2[0] = leftDrive;
            coord2[1] = rightDrive;
        return coord2;
	}
	
	//Makes the robot turn to a certain angle and stop. Increments action when done. maintainAngle() is better.
	public static void setRobotAngle(double target, double tolerance, double speed) {
        double turnNeeded = gyro - target;
        if (Math.abs(turnNeeded) < tolerance) {
        		turn = 0;
        		System.out.println("at angle ––––––––––––––––––––––––––––––––––––––––––––");
        		action++;
        }else if (gyro < target) {
    			turn = speed;
        }else {
        		turn = -speed;
        }
	}

	//first turns robot to angle and then drives at a certain speed for an amount of time. Increments action when drive is done. Not very useful right now
	public static void driveAtAngle(double target, double tolerance, double turnSpeed, double driveTime, double driveSpeed) {
		
		//turning to angle
		if (state.substring(0,1).equals("t")) {
        		double turnNeeded = gyro - target;
        		if (Math.abs(turnNeeded) < tolerance) {
        			turn = 0;
        			System.out.println("stop ––––––––––––––––––––––––––––––––––––––––––––");
        			state = "d" + Integer.toString(action);
        			System.out.println(state);
        			count = 0;
        		}else if (gyro < target) {
        			turn = turnSpeed;
        			System.out.println("right");
        		}else {
        			turn = -turnSpeed;
        			System.out.println("left");
        		}
		}
		
		//driving forward
		if (state.substring(0,1).equals("d")) {
			if (count < target) {
        			System.out.println("forward");
        			drive = driveSpeed;
			}else {
    				System.out.println("stop");
    				drive = 0;
    				action++;
    				state = "t" + Integer.toString(action);
        			System.out.println(state);
			}
		}
	}

	//makes the robot run for a certain amount of time (target) for a certain speed. Increments action when done. accelRun() is better.
	public static void timedRun(double speed, double target) {
        if (count < target) {
        		drive = speed;
        		System.out.println("forward");
        }else {
        		drive = 0;
    			System.out.println("at position +++++++++++++++++++++++++++++++++++++++++++");
    			action++;
        }
	}
	
	//makes the "robot" turn to and stay at a certain angle (target). Input is gyro. Runs repeatedly and does not increment action when done
	public static void maintainAngle(double target, double tolerance, double turnSpeed) {
        double turnNeeded = gyro - target;
        if (Math.abs(turnNeeded) < tolerance) {
        	turn = 0;
//        		System.out.println("at angle ––––––––––––––––––––––––––––––––––––––––––––");
        }else if (gyro < target) {
        		turn = turnSpeed;
//    			System.out.println("right");
        }else {
        		turn = -turnSpeed;
//        		System.out.println("left");
        }
	}
	
	//makes the robot accelerate to the target speed and slow down back to 0 in a certain time by changing the drive variable. Increments action when done
	public static void accelRun(double targetSpeed, double time) {
        if (count < time + 1) {
    			double currentSpeed = (Math.cos(Math.PI * count * 2 / time) - 1) * (-targetSpeed / 2);
        		drive = currentSpeed;
        }else {
        		drive = 0;
    			System.out.println("at position +++++++++++++++++++++++++++++++++++++++++++");
    			count = 0;
    			action++;
        }
	}
	
	
	
	
	
	
	public static void main(String[] args) {
		
		 while(action <= 4){		//stops loop when all actions are run
			 
//GET MOUSE POSITION
			 count++;			//keeps track of "time"
			 rawGyro = (MouseInfo.getPointerInfo().getLocation().x)  ;	//simulates input from gyro with mouse location (right-to-left)
			 gyro = (Math.round(360*((rawGyro / 1439) - 0.5)));			//scales info from mouse (0 to 1439) to a gyro-like number (-180 to 180)
			 
			 
//RUN TRANSFORM MOTOR (not being used right now)
			double[] inputCoord = new double[2];								//start transform motor stuff
			double[] outputCoord = new double[2];
			inputCoord[0] = turn;
			inputCoord[1] = drive;
			outputCoord[0] = 0.0;
			outputCoord[1] = 0.0;
			double[] endCoord = transformMotor(inputCoord, outputCoord);
			double lPower = endCoord[0];	//power to assign to right motor
			double rPower = endCoord[1];	//power to assign to right motor		//end transform motor stuff
	
			
//DISPLAY IN CONSOLE
			long disTurn = Math.round(turn*20) + 10;		//rounds and scales turn to a displayable amount
			long disDrive = Math.round(drive*20) + 10;	//rounds and scales drive to a displayable amount
			
			System.out.println("start +++++++++++++++++++++++++++++++++++++++++++++++++ " + action); //indicates the start boundary and action number
			for (int rows = 0; rows < 25; rows++) {	//makes 24 rows
				System.out.println("");	//starts empty row
				if (rows == disDrive + 1) {	//finds the correct row
					for (int leftColumns = 0; leftColumns < disTurn; leftColumns++) {	
						System.out.print("-");	//prints the dashes to the left of the point
					}
					System.out.print("0");	//prints the point where the x axis is the turn and y axis is the drive (like a joystick)
					for (int rightColumns = 0; rightColumns < (21 - disTurn); rightColumns++) {
						System.out.print("-");	//prints the dashes to the right of the point
					}
				}
			}
			System.out.println("end +++++++++++++++++++++++++++++++++++++++++++++++++"); //indicates the end boundary

			
			
//EXAMPLE AUTO ACTIONS
			 if (action == 0) {
				 accelRun(0.5, 20);				//once accelRun is finished, it moves on to the next action
				 maintainAngle(90, 5, 0.2);
			 }
			 if (action == 1) {
				 accelRun(-0.5, 20);
				 maintainAngle(90, 5, 0.2);
			 }
			 if (action == 2) {
				 accelRun(0.2, 20);
				 maintainAngle(90, 5, 0.2);
			 }
			 if (action == 3) {
				 accelRun(-0.3, 20);
				 maintainAngle(90, 5, 0.2);
			 }
			 if (action == 4) {
				 System.out.println("done");
				 action++;
			 }
			 //if adding or deleting actions be sure to change the total number of actions in big while loop
			
		      
		      
		      
		      
		      
//DELAY
		      try{
		            Thread.sleep(250); //change to 50 for actual FMS speed
		        }catch(InterruptedException ex){
		            Thread.currentThread().interrupt();
		        }
		 }
	}
}

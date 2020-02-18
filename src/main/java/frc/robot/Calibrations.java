package frc.robot;

public class Calibrations
{

    public class CAN_ID {
        public final static int pigeon = 20;
        public final static int driveLeft1 = 3;
        public final static int driveLeft2 = 4;
        public final static int driveRight1 = 1;
        public final static int driveRight2 = 2;
        public final static int shooter1 = 5;
        public final static int shooter2 = 6;
        public final static int indexer = 12;
        public final static int conveyor = 13;
        public final static int carousel = 11;
        public final static int intakeMotor1 = 9;
        public final static int intakeMotor2 = 10;
        public final static int intakeLifter = 14;
        public final static int pcm = 0;
     }
    public class shooter_PID{
        public final static double kP = 1e-3; 
        public final static double kI = 3e-6;
        public final static double kD = 2e-7; 
        public final static double kIz = 225; 
        public final static double kFF = 0.000137862*1.3; 
        public final static double kMaxOutput = 1; 
        public final static double kMinOutput = -0.15;
    }
    public class intake_PID{
        public final static double kP = 1e-3; 
        public final static double kI = 3e-6;
        public final static double kD = 2e-7; 
        public final static double kIz = 225; 
        public final static double kFF = 0.000137862*1.08; 
        public final static double kMaxOutput = 1; 
        public final static double kMinOutput = -0.5;
        public final static int packagePosition = 500;
        public final static int groundPosition = 0;
        public final static int limelitePosition = 250;
        public final static int middlePosition = 350;
    }
    public class ballSuperviserVals{
        public final static int shooterCurrentLimit = 40;
        public final static int intakeArmCurrentLimit = 30;
        public final static double intakeStandardPower = 0.75;
        public final static double indexerPower = 0.75;
    }
}